CREATE USER "backup-prod";
CREATE USER "backup-dev";
CREATE DATABASE "telemetry-backup-prod" with owner "backup-prod";
CREATE DATABASE "telemetry-backup-dev" with owner "backup-dev";
ALTER USER "backup-prod" with PASSWORD '7Dr0F355L83XEb5';
ALTER USER "backup-dev" with PASSWORD 'Bf5UL278yveI4k3';



-- =========================================================
CREATE USER "backup-reader-prod";
ALTER USER "backup-reader-prod" with PASSWORD '7Dr0F355L83XCB5';
comment on role "backup-reader-prod" is 'User with read only access on telemetry-backup-prod';
grant select on all tables in schema public to "backup-reader-prod";

-- =============================
CREATE USER "mlafleur";
ALTER USER "mlafleur" with PASSWORD '7Dr0F355L83XCB5';
comment on role "mlafleur" is 'User ID for mlafleur@canalbarge.com telemetry-backup-prod';
grant select on all tables in schema public to "backup-reader-prod" ;

-- ===============================
CREATE USER "nsupple";
ALTER USER "nsupple" with PASSWORD '7SD0F426L83XNS5';
comment on role "nsupple" is 'User ID for mlafleur@canalbarge.com telemetry-backup-prod';
grant select on all tables in schema public to "nsupple" ;