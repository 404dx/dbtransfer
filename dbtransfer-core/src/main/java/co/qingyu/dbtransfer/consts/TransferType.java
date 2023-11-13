package co.qingyu.dbtransfer.consts;

import lombok.Getter;

@Getter
public enum TransferType {

    TRASFER("TRANSFER"), SQL("SQL");
    final String value;

    TransferType(String value) {
        this.value = value;
    }

}
