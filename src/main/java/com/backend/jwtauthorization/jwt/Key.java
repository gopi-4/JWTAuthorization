package com.backend.jwtauthorization.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Key {
    public String n;
    public String alg;
    public String kty;
    public String e;
    public String kid;
    public String use;
}
