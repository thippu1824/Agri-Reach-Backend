package com.agrireach.employee.entity;
import com.agrireach.user.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )

    private User user;
    @Column(
        name = "employee_code",
        nullable = false,
        unique = true,
        length = 50
    )

    private String employeeCode;
    @Column(name = "full_name", nullable = false, length = 150)

    private String fullName;
    @Column(name = "phone_number", length = 20)

    private String phoneNumber;
    @Column(name = "is_active", nullable = false)

    private boolean active = true;
    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )

    private OffsetDateTime createdAt;
    @Column(
        name = "updated_at",
        nullable = false,
        insertable = false,
        updatable = false
    )

    private OffsetDateTime updatedAt;

    public Employee() {
    }
    public UUID getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getEmployeeCode() {
        return employeeCode;
    }
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}