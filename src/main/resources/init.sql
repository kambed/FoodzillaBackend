create table recipe
(
    id                    int auto_increment
        primary key,
    name                  varchar(250)  not null,
    description            varchar(1000) null,
    time_of_preparation   int           null,
    number_of_steps       int           null,
    steps                 blob          null,
    number_of_ingredients int           null,
    calories              int           null,
    fat                   int           null,
    sugar                 int           null,
    sodium                int           null,
    protein               int           null,
    saturated_fat         int           null,
    carbohydrates         int           null
);

create table ingredient
(
    id   int auto_increment
        primary key,
    name varchar(250) null
);

create table recipe_ingredient
(
    id            int auto_increment
        primary key,
    recipe_id     int not null,
    ingredient_id int not null,
    constraint recipe_ingredient_ingredient_null_fk
        foreign key (ingredient_id) references ingredient (id),
    constraint recipe_ingredient_recipe_null_fk
        foreign key (recipe_id) references recipe (id)
);

create table tag
(
    id   int auto_increment
        primary key,
    name varchar(250) null
);

create table recipe_tag
(
    id        int auto_increment
        primary key,
    recipe_id int not null,
    tag_id    int not null,
    constraint recipe_tag_Recipe_null_fk
        foreign key (recipe_id) references recipe (id),
    constraint recipe_tag_tag_null_fk
        foreign key (tag_id) references tag (id)
);

create table user
(
    id int auto_increment
        primary key
);

create table reviews
(
    id        int auto_increment
        primary key,
    user_id   int           not null,
    recipe_id int           not null,
    review    varchar(1000) null,
    rating    int           null,
    constraint reviews_recipe_null_fk
        foreign key (recipe_id) references recipe (id),
    constraint reviews_user_null_fk
        foreign key (user_id) references user (id)
);