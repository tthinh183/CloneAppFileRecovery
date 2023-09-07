package com.app.allfilerecovery.view.settings

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.RelativeLayout
import android.widget.Toast
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AppOpenManager
import com.app.allfilerecovery.BuildConfig
import com.app.allfilerecovery.R
import com.app.allfilerecovery.callback.IBaseListener
import com.app.allfilerecovery.dialog.DialogExitApp
import com.app.allfilerecovery.dialog.RatingDialog
import com.app.allfilerecovery.local.SharePrefUtils
import com.app.allfilerecovery.local.SystemUtil
import com.app.allfilerecovery.utils.AdsUtils
import com.app.allfilerecovery.view.language.LanguageActivity
import com.app.allfilerecovery.view.others.AboutActivity
import com.app.allfilerecovery.view.others.PolicyActivity
import com.app.allfilerecovery.view.others.SimpleScreenActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.dialog_tips.*

class InfoScreenActivity : SimpleScreenActivity() {
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    private var dialogExitApp: DialogExitApp? = null
    private var idInter = ""
    private var idNative = ""
    private var idBanner = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.setLocale(baseContext)
        setContentView(R.layout.activity_info)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences = defaultSharedPreferences
        editor = defaultSharedPreferences.edit()
        configMediationProvider()

        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId)

        preferences = getSharedPreferences("Rating", Context.MODE_PRIVATE)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnLang.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        btnTip.setOnClickListener {
            showDialogTips()
        }

        btnRate.setOnClickListener {
            showRateSettingDialog()
        }

        displayRating()

        btnFeedbackApp.setOnClickListener {
            AppOpenManager.getInstance().disableAdResumeByClickAction()
            val uriText =
                """
                 mailto:${SharePrefUtils.email},${SharePrefUtils.email1}?subject=${SharePrefUtils.subject}&body=
                 Content: 
                 """.trimIndent()
            val uri = Uri.parse(uriText)
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = uri
            try {
                startActivity(Intent.createChooser(sendIntent, getString(R.string.Send_Email)))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@InfoScreenActivity,
                    getString(R.string.There_is_no),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this@InfoScreenActivity, AboutActivity::class.java))
        }
        btnPolicy.setOnClickListener {
            startActivity(
                Intent(
                    this@InfoScreenActivity,
                    PolicyActivity::class.java
                )
            )
        }

        btnMoreApp.setOnClickListener {
            AppOpenManager.getInstance().disableAdResumeByClickAction()
            startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(getString(R.string.more_app))
                )
            )
        }
    }

    private fun displayRating() {
        preferences?.getBoolean("IS_RATED", false)?.let { rated ->
            if (rated) {
                btnRate.visibility = View.GONE
                view.visibility = View.GONE
            } else {
                btnRate.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialogTips() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.requestFeature(1)
        dialog.setContentView(R.layout.dialog_tips)
        dialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog.window!!.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        dialog.window!!.statusBarColor = resources.getColor(R.color.colorPrimary)
        dialog.imgBackTips.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRateSettingDialog() {
        val ratingDialog = RatingDialog(this)

        ratingDialog.init(this, object : RatingDialog.OnPress {
            override fun send() {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                val uriText =
                    """
                 mailto:${SharePrefUtils.email},${SharePrefUtils.email1}?subject=${SharePrefUtils.subjectRate}&body=${
                        getString(
                            R.string.app_name
                        )
                    }
                 
                 Rate : ${ratingDialog.rating} (star)
                 Content: 
                 """.trimIndent()
                val uri = Uri.parse(uriText)
                val sendIntent = Intent(Intent.ACTION_SENDTO)
                sendIntent.data = uri
                try {
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.Send_Email)))
                    SharePrefUtils.forceRated(this@InfoScreenActivity)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this@InfoScreenActivity,
                        getString(R.string.There_is_no),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ratingDialog.dismiss()
                getSharedPreferences("Rating", MODE_PRIVATE).edit().putBoolean("IS_RATED", true)
                    .apply()
                displayRating()
            }

            override fun rating() {
                AppOpenManager.getInstance().disableAdResumeByClickAction()
                manager = ReviewManagerFactory.create(this@InfoScreenActivity)
                val request: Task<ReviewInfo> = manager!!.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reviewInfo = task.result
                        Log.e("ReviewInfo", "" + reviewInfo)
                        val flow: Task<Void> =
                            manager!!.launchReviewFlow(this@InfoScreenActivity, reviewInfo!!)
                        flow.addOnSuccessListener {
                            SharePrefUtils.forceRated(this@InfoScreenActivity)
                            ratingDialog.dismiss()
                            getSharedPreferences("Rating", MODE_PRIVATE).edit()
                                .putBoolean("IS_RATED", true).apply()
                            displayRating()
                        }
                    } else {
                        ratingDialog.dismiss()
                    }
                }
            }

            override fun cancel() {
                SharePrefUtils.increaseCountOpenApp(this@InfoScreenActivity)
                ///System.exit(0);
            }

            override fun later() {
                SharePrefUtils.increaseCountOpenApp(this@InfoScreenActivity)
                ratingDialog.dismiss()
            }
        })
        try {
            ratingDialog.show()
        } catch (e: BadTokenException) {
            e.printStackTrace()
        }
    }

    private fun showDialogExit() {
        dialogExitApp = DialogExitApp(this, object : IBaseListener {
            override fun onExit() {
                if (dialogExitApp!!.isShowing) {
                    dialogExitApp!!.dismiss()
                    finishAffinity()
                }
            }

            override fun onCancel() {
                if (dialogExitApp!!.isShowing) {
                    dialogExitApp!!.dismiss()
                }
            }
        })
        val w = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val h = ViewGroup.LayoutParams.WRAP_CONTENT
        dialogExitApp!!.window!!.setLayout(w, h)
        dialogExitApp!!.show()
    }

    override fun onResume() {
        super.onResume()
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkConnected = false
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                networkConnected = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                networkConnected = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                networkConnected = true
            }
        }

        if (!networkConnected) {
            include.visibility = View.GONE
        } else {
            include.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun configMediationProvider() {
        //idNative = BuildConfig.native_intro
        //idBanner = BuildConfig.banner_all
    }
}
