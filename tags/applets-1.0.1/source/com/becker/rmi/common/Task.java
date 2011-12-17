package com.becker.rmi.common;

import java.io.Serializable;

public interface Task extends Serializable {
    Object execute();
}
