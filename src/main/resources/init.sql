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
    carbohydrates         float null,
    image                 blob null
);

ALTER TABLE recipe AUTO_INCREMENT=537717;

create table ingredient
(
    id   int auto_increment
        primary key,
    name varchar(250) null
);

ALTER TABLE ingredient AUTO_INCREMENT=14907;

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

ALTER TABLE recipe_ingredient AUTO_INCREMENT=2102984;

create table tag
(
    id   int auto_increment
        primary key,
    name varchar(250) null
);

ALTER TABLE tag AUTO_INCREMENT=553;

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

ALTER TABLE recipe_tag AUTO_INCREMENT=4141689;

create table customer
(
    id int auto_increment
        primary key,
    firstname varchar(250) not null,
    lastname varchar(250) not null,
    username varchar(250) not null,
    password varchar(250) not null
);

ALTER TABLE customer AUTO_INCREMENT=432123;

create table review
(
    id        int auto_increment
        primary key,
    customer_id   int not null,
    recipe_id int not null,
    review    blob null,
    rating    int null,
    constraint review_recipe_null_fk
        foreign key (recipe_id) references recipe (id),
    constraint review_customer_null_fk
        foreign key (customer_id) references customer (id)
);

ALTER TABLE review AUTO_INCREMENT=581254;

create table customer_recipe
(
    id        int auto_increment
        primary key,
    customer_id int not null,
    recipe_id    int not null,
    constraint customer_recipe_Customer_null_fk
        foreign key (customer_id) references customer (id),
    constraint customer_recipe_Recipe_null_fk
        foreign key (recipe_id) references recipe (id)
);

ALTER TABLE customer_recipe AUTO_INCREMENT=1;

CREATE VIEW preference AS
SELECT customer.id AS customer_id, r.recipe_id, r.rating
FROM customer JOIN review r on customer.id = r.customer_id;

create table recently_viewed_recipes
(
    id   int auto_increment primary key,
    customer_id int not null,
    recipe_id int not null,
    constraint recently_viewed_recipes_Customer_null_fk
        foreign key (customer_id) references customer (id),
    constraint recently_viewed_recipes_Recipe_null_fk
        foreign key (recipe_id) references recipe (id)
);