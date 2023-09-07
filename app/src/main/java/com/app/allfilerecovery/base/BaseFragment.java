package com.app.allfilerecovery.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.fragment.app.Fragment;

import com.app.allfilerecovery.local.SystemUtil;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {
    protected T binding;
    protected abstract T setViewBinding(LayoutInflater inflater, @Nullable ViewGroup viewGroup);
    protected abstract void initView();

    //listen to user action here
    protected abstract void viewListener();

    //listen to DB change here
    protected abstract void dataObservable();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SystemUtil.setLocale(requireActivity());
        binding = setViewBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        viewListener();
        dataObservable();
    }
}
