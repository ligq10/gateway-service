package com.changhongit.loving.response;

import com.changhongit.loving.utils.MessageUtils;

public class CommonResponse {
    private static String HEADER = "A R";
    private String seq;
    private String commaddr;
    private String status;
    private static String END = "\r\n";

    public CommonResponse(String seq, String commaddr, String status) {
        super();
        this.seq = seq;
        this.commaddr = commaddr;
        this.status = status;
    }

    private String len() {
        return MessageUtils.getLengthAscii(String.format("%s %s %s |%s|%s ",
                HEADER, seq, commaddr, "0x0000", status));
    }

    private String verifycode() {
        return MessageUtils.getVerifycodeAscii(String.format(
                "%s %s %s |%s|%s ", HEADER, seq, commaddr, len(), status));
    }

    public String formatToString() {
        return String.format("%s %s %s |%s|%s %s%s", HEADER, seq, commaddr,
                len(), status, verifycode(), END);
    }
}
