create table item_producer (
	item_id bigint not null,
	producer_id bigint not null,
	constraint item_producer_pkey primary key (item_id, producer_id)
);

alter table ${flyway:defaultSchema}.item_producer owner to ${flyway:user};

create table category (
	id bigint generated by default as identity constraint category_pk primary key
);

alter table ${flyway:defaultSchema}.category owner to ${flyway:user};

create table category_details (
	id bigint generated by default as identity constraint category_details_pk primary key,
	description varchar(255),
	lang varchar(255),
	name varchar(255),
	category_id bigint constraint category_id_fk references category
);

alter table ${flyway:defaultSchema}.category_details owner to ${flyway:user};

create table image (
	id bigint generated by default as identity constraint image_pk primary key,
	alt varchar(255),
	url varchar(255)
);

alter table ${flyway:defaultSchema}.image owner to ${flyway:user};

create table producer (
	id bigint generated by default as identity constraint producer_pk primary key,
	image_id bigint constraint image_fk references image
);

alter table producer owner to ${flyway:user};

create table item (
	id bigint generated by default as identity constraint item_pk primary key,
	stock integer not null,
	value_gross numeric(19,2),
	vat double precision not null,
	image_id bigint constraint image_id_fk references image,
	producer_id bigint constraint producer_id_fk references producer
);

alter table ${flyway:defaultSchema}.item owner to ${flyway:user};

create table item_category (
	item_id bigint not null constraint item_id_fk references item,
	category_id bigint not null constraint category_id_fk references category,
	constraint item_category_pk primary key (item_id, category_id)
);

alter table ${flyway:defaultSchema}.item_category owner to ${flyway:user};

create table item_details (
	id bigint generated by default as identity constraint item_details_pk primary key,
	description text,
	lang varchar(255),
	name varchar(255),
	item_id bigint constraint item_details_fk references item
);

alter table ${flyway:defaultSchema}.item_details owner to ${flyway:user};

create table item_details_image (
	item_details_id bigint not null constraint item_details_pk references item_details,
	images_id bigint not null constraint images_id_uk unique constraint images_id_fk references image,
	constraint item_details_image_pk primary key (item_details_id, images_id)
);

alter table ${flyway:defaultSchema}.item_details_image owner to ${flyway:user};

create table producer_details (
	id bigint generated by default as identity constraint producer_details_pk primary key,
	description varchar(255),
	lang varchar(255),
	name varchar(255),
	producer_id bigint constraint producer_id_fk references producer
);

alter table ${flyway:defaultSchema}.producer_details owner to ${flyway:user};

create table users (
	id bigint generated by default as identity constraint users_pk primary key,
	email varchar(255),
	firstname varchar(255),
	lastname varchar(255)
);

alter table ${flyway:defaultSchema}.users owner to ${flyway:user};

create table address (
	id bigint generated by default as identity constraint address_pk primary key,
	city varchar(255),
	local varchar(255),
	street varchar(255),
	zip varchar(255),
	user_id bigint constraint user_id_fk references users
);

alter table ${flyway:defaultSchema}.address owner to ${flyway:user};

create table order_details (
	id bigint generated by default as identity constraint order_details_pk primary key,
	creation_date date,
	order_status varchar(255),
	payment_method varchar(255),
	payment_status varchar(255),
	address_id bigint constraint address_id_fk references address,
	user_id bigint constraint user_id_fk references users,
    order_details jsonb
);

alter table ${flyway:defaultSchema}.order_details owner to ${flyway:user};

create table user_permissions (
	user_id bigint not null constraint user_id_fk references users,
	permissions varchar(255)
);

alter table ${flyway:defaultSchema}.user_permissions owner to ${flyway:user};

-- SEQUENCES ---------------------------------------------------

alter sequence item_id_seq
    increment by 1
    minvalue 20
    start 20
    restart 20;
alter sequence ${flyway:defaultSchema}.item_id_seq owner to ${flyway:user};



