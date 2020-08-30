package com.buddy.buddyapp.Email;

public class GmailSender {

    public static void GSender(String Message, String Subject)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender("ultimatefreedom0@gmail.com",
                            "buddyapp4");
                    sender.sendMail(Subject, Message,
                            "ultimatefreedom0@gmail.com", "buddyapp4@gmail.com");
                } catch (Exception e) {

                }
            }

        }).start();
    }
}
