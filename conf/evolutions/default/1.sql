# --- !Ups
SET NAMES utf8;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'user name',
  `update_time` int(10) NOT NULL COMMENT 'last update timestamp',
  `init_time` int(10) NOT NULL COMMENT 'init timestamp',
  `tombstone` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE IF EXISTS user;