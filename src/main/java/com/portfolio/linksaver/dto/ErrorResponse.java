package com.portfolio.linksaver.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timeStamp
) {} 
    

