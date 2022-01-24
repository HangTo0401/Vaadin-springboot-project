package com.example.demo.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this != obj || this.getClass() != obj.getClass()) {
            return false;
        }

        AbstractEntity otherObj = (AbstractEntity) obj;
        if (otherObj.getId() == null || getId() == null || otherObj.getId() != this.getId()) {
            return false;
        }

        return true;
    }
}
