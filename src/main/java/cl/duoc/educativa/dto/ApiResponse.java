package cl.duoc.educativa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String mensaje;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .mensaje(mensaje)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return ApiResponse.<T>builder()
                .success(false)
                .mensaje(mensaje)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
