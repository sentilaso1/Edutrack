package com.example.edutrack.accounts.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.Date;

@Entity
@Table(name = "properties")
public class Property {
        @Id
        @Column(columnDefinition = "BINARY(16)")
        private UUID id;

        @Column(name = "prop_key", nullable = false, unique = true)
        private String key;

        @Column(name = "prop_value", nullable = false, columnDefinition = "TEXT")
        private String value;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column(name = "created_date", nullable = false)
        @Temporal(TemporalType.TIMESTAMP)
        private Date createdDate;

        @Column(name = "updated_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date updatedDate;

        public Property() {
                this.id = UUID.randomUUID();
                this.createdDate = new Date();
        }

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public String getKey() {
                return key;
        }

        public void setKey(String key) {
                this.key = key;
        }

        public String getValue() {
                return value;
        }

        public void setValue(String value) {
                this.value = value;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public Date getCreatedDate() {
                return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
                this.createdDate = createdDate;
        }

        public Date getUpdatedDate() {
                return updatedDate;
        }

        public void setUpdatedDate(Date updatedDate) {
                this.updatedDate = updatedDate;
        }
}