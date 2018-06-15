DROP TABLE IF EXISTS gis_road;
CREATE TABLE gis_road
(
    gid serial PRIMARY KEY,
    segid varchar(150),
    previoussegid numeric(10,0),
    nextsegid numeric(10,0),
    frc integer,
    distance varchar(50),
    lanes integer,
    startlat numeric,
    startlong numeric,
    endlat numeric,
    endlong numeric,
    roadnumber varchar(50),
    roadname varchar(100),
    roaddirection varchar(1),
    roadlist varchar(254),
    country varchar(100),
    state varchar(100),
    county varchar(100),
    city varchar(100),
    sliproad integer,
    bearing varchar(1),
    speedlimit integer,
    coordinates text,
    geom geometry(MultiLineString,4326),
    lastmodified timestamp  with time zone,

    objectId numeric(10,0),
    strt numeric(10,0),
    fsnd numeric(10,0),
    head numeric(10,0),
    refe integer,
    hier integer,
    salikname varchar(200),
    CONSTRAINT gis_road_pkey PRIMARY KEY (gid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_road
  OWNER TO gis;

  
DROP TABLE IF EXISTS gis_road_history;

CREATE TABLE gis_road_history
(
  id serial PRIMARY KEY,
  country character varying(100),
  state character varying(100),
  county character varying(100),
  city character varying(254),
  versionNum character varying(30),
  createTime timestamp  with time zone,
  CONSTRAINT gis_road_history_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_road_history
  OWNER TO gis;

--CREATE INDEX index_gis_road ON gis_road (segid);
--CREATE INDEX index_gis_traffic ON gis_traffic (segid,fore);
--CREATE INDEX index_gis_traffic_history ON gis_traffic_history (segid,traffictime);
-- DROP FUNCTION fms_roadtraffic_gis_func(text, text, text);

CREATE  FUNCTION fms_roadtraffic_gis_func(
    IN start_time text,
    IN end_time text,
	IN fore text)

  RETURNS TABLE(segid character varying, speed numeric, speedbucket integer, traffictime timestamp with time zone, geom geometry) AS

$BODY$
DECLARE
	where_clauses text;
BEGIN
	where_clauses := 'select h.segid, h.speed, h.speedbucket,h.traffictime, r.geom from gis_traffic h,gis_road r where h.segid = r.segid ';
	IF start_time is null or start_time = '' or end_time is null or end_time = ''  then
	    where_clauses := where_clauses;
	else
		where_clauses := concat( where_clauses, 'and (traffictime between to_timestamp(''', start_time, ''', ''yyyy-mm-dd HH24:MI:ss'') and to_timestamp(''', end_time, ''', ''yyyy-mm-dd HH24:MI:ss''))');
	end if;

	IF fore is null or fore = ''   then
	    where_clauses := where_clauses;
	else
		where_clauses := concat( where_clauses, 'and  fore = ' ,fore ) ;
	end if;

	RETURN QUERY EXECUTE where_clauses;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fms_roadtraffic_gis_func(text, text,text)
  OWNER TO gis;

-- DROP FUNCTION fms_roadtraffichistory_gis_func(text, text, text);

CREATE  FUNCTION fms_roadtraffichistory_gis_func(
    IN start_time text,
    IN end_time text,
	IN fore text)

  RETURNS TABLE(segid character varying, speed numeric, speedbucket integer, traffictime timestamp with time zone, geom geometry) AS

$BODY$
DECLARE
	where_clauses text;
BEGIN
	where_clauses := 'select h.segid, h.speed, h.speedbucket,h.traffictime, r.geom from gis_traffic_history h,gis_road r where h.segid = r.segid ';
	IF start_time is null or start_time = '' or end_time is null or end_time = ''  then
	    where_clauses := where_clauses;
	else
		where_clauses := concat( where_clauses, 'and (traffictime between to_timestamp(''', start_time, ''', ''yyyy-mm-dd HH24:MI:ss'') and to_timestamp(''', end_time, ''', ''yyyy-mm-dd HH24:MI:ss''))');
	end if;

	IF fore is null or fore = ''   then
	    where_clauses := where_clauses;
	else
		where_clauses := concat( where_clauses, 'and  fore = ' ,fore ) ;
	end if;

	RETURN QUERY EXECUTE where_clauses;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fms_roadtraffichistory_gis_func(text, text,text)
  OWNER TO gis;


DROP TABLE IF EXISTS gis_salik;
CREATE TABLE gis.gis_salik
(
  salikname character varying(150),
  lat numeric,
  lon numeric,
  roadname  character varying(150)
);

insert into gis_salik(salikname,lat,lon) values ('Al Garhoud Bridge Salik Gate','25.2308363','55.3350997');
insert into gis_salik(salikname,lat,lon) values ('Al Garhoud Bridge Salik Gate','25.230857','55.335743');
insert into gis_salik(salikname,lat,lon) values ('Al Maktoum Bridge Salik Gate','25.2309819','55.335729');
insert into gis_salik(salikname,lat,lon) values ('Al Garhoud Bridge Salik Gate','25.231474','55.335835');
insert into gis_salik(salikname,lat,lon) values ('Al Safa Salik Gate','25.193804','55.2613305');
insert into gis_salik(salikname,lat,lon) values ('Al Safa Salik Gate','25.193452','55.2614005');
insert into gis_salik(salikname,lat,lon) values ('Al Barsha Salik Gate','25.115991','55.1910415');
insert into gis_salik(salikname,lat,lon) values ('Al Barsha Salik Gate','25.115755','55.191746');
insert into gis_salik(salikname,lat,lon) values ('Airport Tunnel Salik Gate','25.248984','55.387280');
insert into gis_salik(salikname,lat,lon) values ('Airport Tunnel Salik Gate','25.249030','55.387055');
insert into gis_salik(salikname,lat,lon) values ('Al Mamzar North Salik Gate','25.297582','55.361479');
insert into gis_salik(salikname,lat,lon) values ('Al Mamzar North Salik Gate','25.296034','55.361248');
insert into gis_salik(salikname,lat,lon) values ('Al Mamzar South Salik Gate','25.286168','55.359071');
insert into gis_salik(salikname,lat,lon) values ('Al Mamzar South Salik Gate','25.286148','55.358866');
insert into gis_salik(salikname,lat,lon) values ('Al Mamzar South Salik Gate','25.285561','55.359259');
