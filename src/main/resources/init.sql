create table recipe
(
    id                    int auto_increment
        primary key,
    name                  varchar(250) null,
    description           blob null,
    time_of_preparation   int null,
    number_of_steps       int null,
    steps                 blob null,
    number_of_ingredients int null,
    calories              float null,
    fat                   float null,
    sugar                 float null,
    sodium                float null,
    protein               float null,
    saturated_fat         float null,
    carbohydrates         float null
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

create table review
(
    id        int auto_increment
        primary key,
    user_id   int not null,
    recipe_id int not null,
    review    blob null,
    rating    int null,
    constraint review_recipe_null_fk
        foreign key (recipe_id) references recipe (id),
    constraint review_user_null_fk
        foreign key (user_id) references user (id)
);