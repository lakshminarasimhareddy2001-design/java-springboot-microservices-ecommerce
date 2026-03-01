package com.eswar.userservice.entity;

import com.eswar.userservice.audit.AbstractAuditingEntity;
import com.eswar.userservice.constants.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 150)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(max = 5)
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @NotBlank
    @Size(max = 15)
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Size(max = 100)
    @Column(name = "address_street")
    private String addressStreet;


    @Size(max = 100)
    @Column(name = "address_city")
    private String addressCity;

    @Size(max = 100)
    @Column(name = "address_country")
    private String addressCountry;


    @Size(max = 20)
    @Column(name = "address_zip_code")
    private String addressZipCode;

    @Column(name = "last_seen")
    private Instant lastSeen;

    @Column(name = "password")
    String password;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

}
