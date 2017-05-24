create TABLE SCODE_HISTORY (
  scode_id   VARCHAR2(30),
  data_src   NUMBER(2),
  frequency  NUMBER,
  updated_at DATE,
  local_server_time date
);
COMMENT ON COLUMN SCODE_HISTORY.data_src is '11:座席指标 12:实时指标';
COMMENT ON COLUMN SCODE_HISTORY.frequency is '7:5分钟 8:天累计 9:月累计,座席指标不区分频度,统一使用7';
COMMENT ON COLUMN SCODE_HISTORY.updated_at is '指标自身生成时间';
COMMENT ON COLUMN SCODE_HISTORY.local_server_time is '写入此表时的时间';
COMMENT ON COLUMN SCODE_HISTORY.scode_id is '指标名';
ALTER TABLE SCODE_HISTORY ADD CONSTRAINT scode_history_pk PRIMARY KEY (scode_id,frequency);