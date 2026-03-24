package com.example.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "User_Goal")
public class User_Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="User_id")
    private User user;

    private String Goal_type;
    private float target_calories;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getGoal_type() { return Goal_type ;}
    public void setGoal_type(String Goal_type) { this.Goal_type = Goal_type ;}

    public float getTarget_calories() { return target_calories;}
    public void setTarget_calories(float target_calogies) { this.target_calories = target_calogies;}

    public User getUser() { return user; }

    public void setUser(User user) {this.user = user;}
    
}
