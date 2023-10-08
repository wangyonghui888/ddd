package com.panda.rcs.logService.service;

import java.util.List;

public interface LogService<T, E> {

    public T saveLog(T logBean);

    public List<T> query(E queryVO);

}
