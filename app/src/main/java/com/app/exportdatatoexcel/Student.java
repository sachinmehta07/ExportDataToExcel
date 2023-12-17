package com.app.exportdatatoexcel;

public class Student {
    private int id;
    private String name;
    private String dep;
    private String roleNo;

    public Student(int id, String name, String dep, String roleNo) {
        this.id = id;
        this.name = name;
        this.dep = dep;
        this.roleNo = roleNo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDep() {
        return dep;
    }

    public String getRoleNo() {
        return roleNo;
    }
}
