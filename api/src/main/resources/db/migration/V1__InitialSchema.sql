CREATE TABLE contact (
    id      BIGSERIAL NOT NULL,
    name    VARCHAR(255) NOT NULL,
    phone   VARCHAR(255),
    CONSTRAINT pk_contact PRIMARY KEY (id),
    CONSTRAINT uk_contact_name UNIQUE (name)
);

CREATE TABLE address_book (
    id      BIGSERIAL NOT NULL,
    name    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_address_book PRIMARY KEY (id),
    CONSTRAINT uk_address_book_name UNIQUE (name)
);

CREATE TABLE address_book_detail (
    fk_book_id      BIGINT NOT NULL,
    fk_contact_id   BIGINT NOT NULL,
    CONSTRAINT pk_address_book_detail PRIMARY KEY (fk_book_id, fk_contact_id),
    CONSTRAINT fk_address_book_detail_book_id FOREIGN KEY (fk_book_id) REFERENCES address_book (id) ON DELETE CASCADE,
    CONSTRAINT fk_address_book_detail_contact_id FOREIGN KEY (fk_contact_id) REFERENCES contact (id)
);
