package com.gianx64.skyscheduler;

public class PersonClass {
    int id;
    String name;
    int scheduleStart;
    int scheduleEnd;
    int load;

    public PersonClass() {
        this.name = "";
        this.scheduleStart = 0;
        this.scheduleEnd = 0;
        this.load = 0;
    }

    public PersonClass(String name, int scheduleStart, int scheduleEnd) {
        this.name = name;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
        this.load = 0;
    }

    public PersonClass(int id, String name, int scheduleStart, int scheduleEnd) {
        this.id = id;
        this.name = name;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
        this.load = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScheduleStart() {
        return scheduleStart;
    }

    public void setScheduleStart(int scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public int getScheduleEnd() {
        return scheduleEnd;
    }

    public void setScheduleEnd(int scheduleEnd) { this.scheduleEnd = scheduleEnd; }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public String assign() {
        this.load++;
        return this.name;
    }
}
