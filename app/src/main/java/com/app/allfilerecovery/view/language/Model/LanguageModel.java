package com.app.allfilerecovery.view.language.Model;

public class LanguageModel {
    private String code;
    private String name;
    private boolean active =false;



    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean getActive() {
        return this.active;
    }

    public LanguageModel(String name, String code) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
