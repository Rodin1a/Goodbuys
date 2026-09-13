package com.tu.goodsbuy.global.util;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ScriptWriterUtil {
    private ScriptWriterUtil() {}

    public static void writeScript(HttpServletResponse response, String message) throws IOException {
        write(response, message, "");
    }

    public static void writeAndRedirect(HttpServletResponse response, String message, String path) throws IOException {
        write(response, message, ".then(() => { location.href = " + quote(path) + "; })");
    }

    private static void write(HttpServletResponse response, String message, String continuation) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("<!doctype html><html lang=\"ko\"><head><meta charset=\"UTF-8\">"
                + "<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script></head><body><script>"
                + "Swal.fire({title: " + quote(message) + ", icon: 'info', confirmButtonText: 'OK'})"
                + continuation + ";</script></body></html>");
        response.getWriter().flush();
    }

    private static String quote(String value) {
        return "\"" + new String(JsonStringEncoder.getInstance().quoteAsString(value))
                .replace("<", "\\u003c").replace(">", "\\u003e").replace("&", "\\u0026") + "\"";
    }
}
