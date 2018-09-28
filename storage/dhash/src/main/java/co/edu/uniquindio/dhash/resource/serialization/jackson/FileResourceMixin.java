package co.edu.uniquindio.dhash.resource.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

@JsonIgnoreProperties(value = { "inputStream" })
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public class FileResourceMixin {
}
