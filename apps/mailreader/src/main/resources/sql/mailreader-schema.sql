CREATE TABLE locale
(
  pk_locale char(36),
  locale_code char(9),
  locale_name char(36)
);

CREATE TABLE registration
(
  pk_registration char(36),
  fk_locale char(36),
  username  char(18),
  password  char(18),
  fullname  varchar(36),
  email_from varchar(72),
  email_replyto varchar(72)
);

CREATE TABLE protocol
(
  pk_protocol char(36),
  protocol_code char(9),
  protocol_name char(36)
);

CREATE TABLE subscription
(
  pk_subscription     char(36),
  fk_registration  char(36),
  fk_protocol char(36),
  subscription_host  char(36),
  host_user char(18),
  host_pass char(18),
  host_auto int
);
