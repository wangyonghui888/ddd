package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.JVMQueryDTO;

public interface RCSMonitorService {



    Object getMemory(JVMQueryDTO jVMQueryDTO);

    Object getServiceInfo(JVMQueryDTO jVMQueryDTO);

    Object getSystem(JVMQueryDTO jVMQueryDTO);

    Object getGC(JVMQueryDTO jVMQueryDTO);

    Object getServerName();

	Object getThread(JVMQueryDTO jVMQueryDTO);
}

