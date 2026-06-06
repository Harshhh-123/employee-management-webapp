package net.javaguides.springboot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Designation designation;

    private Double dailyWageRate;
    private Boolean active = true;

    public enum Designation {
        MASON, ELECTRICIAN, PLUMBER, SUPERVISOR, HELPER
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Designation getDesignation() { return designation; }
    public void setDesignation(Designation designation) { this.designation = designation; }
    public Double getDailyWageRate() { return dailyWageRate; }
    public void setDailyWageRate(Double dailyWageRate) { this.dailyWageRate = dailyWageRate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}