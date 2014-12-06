# 数据初始化
INSERT INTO TB_USERS(NAME) VALUES('format');

INSERT INTO TB_ROLES(NAME) VALUES('admin');

INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('add-user','添加用户');
INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('update-user','修改用户');
INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('delete-user','删除用户');
INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('add-dept','添加部门');
INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('update-dept','更新部门');
INSERT INTO TB_AUTHS(NAME,DESCRIPTION) VALUES('add-role','添加角色');

INSERT INTO TB_USER_ROLE(USER_ID,ROLE_ID) VALUES(1,1);

INSERT INTO TB_ROLE_AUTH(ROLE_ID,AUTH_ID) VALUES(1,1);
INSERT INTO TB_ROLE_AUTH(ROLE_ID,AUTH_ID) VALUES(1,2);
INSERT INTO TB_ROLE_AUTH(ROLE_ID,AUTH_ID) VALUES(1,3);

INSERT INTO TB_USER_AUTH(USER_ID,AUTH_ID) VALUES(1,4);
INSERT INTO TB_USER_AUTH(USER_ID,AUTH_ID) VALUES(1,5);
