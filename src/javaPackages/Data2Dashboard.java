/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaPackages;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author khorzeyi
 */
public class Data2Dashboard {
    private StringProperty time;
    private IntegerProperty elise;
    private IntegerProperty evora;
    private IntegerProperty exige;

    public Data2Dashboard(String time, int elise, int evora, int exige) {
        this.time = new SimpleStringProperty(time);
        this.elise = new SimpleIntegerProperty(elise);
        this.evora = new SimpleIntegerProperty(evora);
        this.exige = new SimpleIntegerProperty(exige);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public IntegerProperty eliseProperty() {
        return elise;
    }

    public int getElise() {
        return elise.get();
    }

    public void setElise(int elise) {
        this.elise.set(elise);
    }

    public IntegerProperty evoraProperty() {
        return evora;
    }

    public int getEvora() {
        return evora.get();
    }

    public void setEvora(int evora) {
        this.evora.set(evora);
    }

    public IntegerProperty exigeProperty() {
        return exige;
    }

    public int getExige() {
        return exige.get();
    }

    public void setExige(int exige) {
        this.exige.set(exige);
    }
    
    public int getTotal() {
        return elise.get() + evora.get() + exige.get();
    }
}
