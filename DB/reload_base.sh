#!/bin/bash
rm MsgDB
sqlite3 MsgDB < ../src/message/sql/createSQLiteDB.sql
