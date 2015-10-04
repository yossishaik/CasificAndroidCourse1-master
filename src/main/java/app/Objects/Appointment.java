package app.Objects;

public class Appointment {

    public String service;
    public String giver;
    public String date;
    public String time;
    public String name;
    public String phone;
    public String email;

    public Appointment(String service, String giver, String date, String time, String name, String phone, String email){
        this.service = service;
        this.giver = giver;
        this.date = date;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
