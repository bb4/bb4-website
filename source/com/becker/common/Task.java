package com.becker.common;

import java.io.Serializable;

public interface Task extends Serializable {
    Object execute();
}
