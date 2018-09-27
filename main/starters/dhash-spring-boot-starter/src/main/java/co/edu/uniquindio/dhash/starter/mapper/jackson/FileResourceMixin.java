package co.edu.uniquindio.dhash.starter.mapper.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "inputStream" })
public class FileResourceMixin {
}
