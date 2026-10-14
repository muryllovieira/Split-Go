package com.splitgo.api.group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity                       
@Table(name = "groups")       
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)   
    private UUID id;

    @Column(nullable = false, length = 100)           
    private String name;

    @JdbcTypeCode(SqlTypes.CHAR)                      
    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @CreationTimestamp                                
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Group() {                               
    }

    public Group(String name, String baseCurrency) {  
        this.name = name;
        this.baseCurrency = baseCurrency;
    }

    public UUID getId() { return id; }                
    public String getName() { return name; }
    public String getBaseCurrency() { return baseCurrency; }
    public Instant getCreatedAt() { return createdAt; }
}