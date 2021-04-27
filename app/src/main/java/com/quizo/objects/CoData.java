package com.quizo.objects;

public class CoData {
    private String active,currently,deaths,recovered,confirmed,from;
    public CoData(){

    }

    public CoData(String currently,String active,String deaths,String recovered,String confirmed,String from){
this.active=active;
this.currently=currently;
this.deaths=deaths;
this.recovered=recovered;
this.confirmed=confirmed;
this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCurrently() {
        return currently;
    }

    public void setCurrently(String currently) {
        this.currently = currently;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }
}
