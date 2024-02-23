package com.in28minutes.rest.webservices.restfulwebservices.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema="Users")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

    @UniqueElements
	private String username;
	private String password;
	private Boolean enabled;
}


