INSERT INTO locale
    (pk_locale, locale_code, locale_name)
VALUES ('01-en','en','English');

INSERT INTO locale
    (pk_locale, locale_code, locale_name)
VALUES ('02-ru','ru','Russian');

INSERT INTO locale
    (pk_locale, locale_code, locale_name)
VALUES ('03-ja','ja','Japanese');

INSERT INTO protocol
    (pk_protocol, protocol_code, protocol_name)
VALUES ('01-pop3','pop3','POP3 Protoocol');

INSERT INTO protocol
    (pk_protocol, protocol_code, protocol_name)
VALUES ('02-smtp','smtp','SMTP Protocol');

INSERT INTO registration
    (pk_registration, fk_locale, username, password, fullname, email_from, email_replyto)
VALUES ('01-user','01-en','user','pass','John Q. User','John.User@somewhere.com','');

INSERT INTO subscription
    (pk_subscription, fk_registration, fk_protocol, subscription_host, host_user, host_pass, host_auto)
VALUES ('01-hotmail','01-user','01-pop3','mail.hotmail.com','user1234','bar',0);

INSERT INTO subscription
    (pk_subscription, fk_registration, fk_protocol, subscription_host, host_user, host_pass, host_auto)
VALUES ('02-yahoo','01-user','02-smtp','mail.yahoo.com','jquser','foo',1);
