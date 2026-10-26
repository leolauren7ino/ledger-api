package io.github.leolauren7ino.ledgerapi.usuario;

import java.util.List;

public record MeResponse(String email, String perfil, List<String> autoridades) { }
