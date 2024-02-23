package com.in28minutes.rest.webservices.restfulwebservices.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema="authorities")
public class Authorities {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer username;
	private String authority;
	private Users user;

}
