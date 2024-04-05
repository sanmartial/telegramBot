package org.globaroman.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString
public class User {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;
}
