package com.bsrvch.arbita.dto;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DTO {
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
}
