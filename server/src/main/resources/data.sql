INSERT INTO lead_status (id, order_col, name) VALUES
                                             (nextval ('hibernate_sequence'), '0', 'Новая'),
                                             (nextval ('hibernate_sequence'), '1', 'Проработка ТКП'),
                                             (nextval ('hibernate_sequence'), '2', 'Направлено ТКП'),
                                             (nextval ('hibernate_sequence'), '10', 'Заказ'),
                                             (nextval ('hibernate_sequence'), '99', 'Отложено'),
                                             (nextval ('hibernate_sequence'),  '100', 'Отказ');
