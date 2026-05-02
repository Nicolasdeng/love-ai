package com.loveai.chat.service;

import com.loveai.chat.dto.SimulateRequest;
import com.loveai.chat.dto.SimulateResponse;

public interface ChatSimulateService {
    SimulateResponse simulate(SimulateRequest request);
}