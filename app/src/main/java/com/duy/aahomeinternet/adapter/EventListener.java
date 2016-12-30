package com.duy.aahomeinternet.adapter;

import com.duy.aahomeinternet.items.DeviceItem;

public interface EventListener {
    void deviceDetail(int pos, DeviceItem deviceItem);

    void deviceChangeInfo(int pos, DeviceItem deviceItem);

    void deviceDelete(int pos, DeviceItem deviceItem);

}