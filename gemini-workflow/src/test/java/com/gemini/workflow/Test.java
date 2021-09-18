package com.gemini.workflow;

public class Test {
    public static void main(String[] args){
        String referer = "http://localhost:8080/ctms/qc/QcOrder/review.do?s_fid=QC070F03&uuid=5ed9311e-b985-4c76-936e-4205f85f13b5&id=10200&s_buuid=null&s_bid=null&workflow.piid=a31b852a-162a-11ec-a491-ea126530f9af&workflow.taskId=a3223bf9-162a-11ec-a491-ea126530f9af&s_popFlag=1&s_csrfToken=689875970387577&";
        referer = referer.substring(referer.indexOf("?")+1);
        System.out.println(referer);

    }
}
