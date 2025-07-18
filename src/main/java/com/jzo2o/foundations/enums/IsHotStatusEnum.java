package com.jzo2o.foundations.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IsHotStatusEnum {
    hot(1, "热门"),
    notHot(0, "非热门");
    private int status;
    private String description;

    public boolean equals(Integer status) {
        return this.status == status;
    }
    public boolean equals(IsHotStatusEnum isHotStatusEnum) {
        return isHotStatusEnum != null && isHotStatusEnum.status == this.getStatus();
    }
}
