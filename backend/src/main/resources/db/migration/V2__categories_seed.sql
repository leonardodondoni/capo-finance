INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Alimentação', NULL, 'utensils', '#FF6B6B');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Supermercado', (SELECT id FROM categories WHERE name = 'Alimentação'), 'shopping-cart', '#FF8E8E'),
('Restaurante', (SELECT id FROM categories WHERE name = 'Alimentação'), 'chef-hat', '#FF9999'),
('Delivery', (SELECT id FROM categories WHERE name = 'Alimentação'), 'truck', '#FFAAAA'),
('Lanche', (SELECT id FROM categories WHERE name = 'Alimentação'), 'coffee', '#FFBBBB');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Transporte', NULL, 'car', '#4ECDC4');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Combustível', (SELECT id FROM categories WHERE name = 'Transporte'), 'fuel', '#66D9D2'),
('Uber/99', (SELECT id FROM categories WHERE name = 'Transporte'), 'smartphone', '#7DDDD6'),
('Ônibus/Metro', (SELECT id FROM categories WHERE name = 'Transporte'), 'bus', '#94E1DA'),
('Estacionamento', (SELECT id FROM categories WHERE name = 'Transporte'), 'parking-circle', '#AAE5DE'),
('Manutenção Veículo', (SELECT id FROM categories WHERE name = 'Transporte'), 'wrench', '#C1E9E2');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Moradia', NULL, 'home', '#45B7D1');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Aluguel', (SELECT id FROM categories WHERE name = 'Moradia'), 'key', '#5CC3D7'),
('Condomínio', (SELECT id FROM categories WHERE name = 'Moradia'), 'building', '#73CFDD'),
('Energia Elétrica', (SELECT id FROM categories WHERE name = 'Moradia'), 'zap', '#8ADBE3'),
('Água', (SELECT id FROM categories WHERE name = 'Moradia'), 'droplets', '#A1E7E9'),
('Internet', (SELECT id FROM categories WHERE name = 'Moradia'), 'wifi', '#B8F3EF'),
('Gás', (SELECT id FROM categories WHERE name = 'Moradia'), 'flame', '#CFFFF5');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Saúde', NULL, 'heart', '#96CEB4');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Médico', (SELECT id FROM categories WHERE name = 'Saúde'), 'stethoscope', '#A6D4C2'),
('Farmácia', (SELECT id FROM categories WHERE name = 'Saúde'), 'pill', '#B6DAD0'),
('Plano de Saúde', (SELECT id FROM categories WHERE name = 'Saúde'), 'shield', '#C6E0DE'),
('Academia', (SELECT id FROM categories WHERE name = 'Saúde'), 'dumbbell', '#D6E6EC');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Lazer', NULL, 'gamepad-2', '#DDA0DD');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Cinema', (SELECT id FROM categories WHERE name = 'Lazer'), 'film', '#E2B0E2'),
('Streaming', (SELECT id FROM categories WHERE name = 'Lazer'), 'tv', '#E7C0E7'),
('Jogos', (SELECT id FROM categories WHERE name = 'Lazer'), 'joystick', '#ECD0EC'),
('Viagem', (SELECT id FROM categories WHERE name = 'Lazer'), 'plane', '#F1E0F1'),
('Bar/Balada', (SELECT id FROM categories WHERE name = 'Lazer'), 'wine', '#F6F0F6');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Educação', NULL, 'graduation-cap', '#FFD93D');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Cursos', (SELECT id FROM categories WHERE name = 'Educação'), 'book-open', '#FFE066'),
('Livros', (SELECT id FROM categories WHERE name = 'Educação'), 'book', '#FFE799'),
('Material Escolar', (SELECT id FROM categories WHERE name = 'Educação'), 'pen-tool', '#FFEECC');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Tecnologia', NULL, 'smartphone', '#6C5CE7');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Celular', (SELECT id FROM categories WHERE name = 'Tecnologia'), 'phone', '#8B7BEA'),
('Software/Apps', (SELECT id FROM categories WHERE name = 'Tecnologia'), 'download', '#A99AED'),
('Equipamentos', (SELECT id FROM categories WHERE name = 'Tecnologia'), 'monitor', '#C7B9F0');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Vestuário', NULL, 'shirt', '#FD79A8');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Roupas', (SELECT id FROM categories WHERE name = 'Vestuário'), 'shirt', '#FE92B8'),
('Calçados', (SELECT id FROM categories WHERE name = 'Vestuário'), 'footprints', '#FEABC8'),
('Acessórios', (SELECT id FROM categories WHERE name = 'Vestuário'), 'watch', '#FFC4D8');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Financeiro', NULL, 'credit-card', '#00B894');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Taxa Bancária', (SELECT id FROM categories WHERE name = 'Financeiro'), 'landmark', '#26C6A4'),
('Investimentos', (SELECT id FROM categories WHERE name = 'Financeiro'), 'trending-up', '#4DD4B4'),
('Empréstimos', (SELECT id FROM categories WHERE name = 'Financeiro'), 'hand-coins', '#73E2C4'),
('Seguros', (SELECT id FROM categories WHERE name = 'Financeiro'), 'shield-check', '#99F0D4');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Receitas', NULL, 'dollar-sign', '#00D2D3');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Salário', (SELECT id FROM categories WHERE name = 'Receitas'), 'briefcase', '#2DD9DA'),
('Freelance', (SELECT id FROM categories WHERE name = 'Receitas'), 'laptop', '#5AE0E1'),
('Vendas', (SELECT id FROM categories WHERE name = 'Receitas'), 'shopping-bag', '#87E7E8'),
('Investimentos Receita', (SELECT id FROM categories WHERE name = 'Receitas'), 'piggy-bank', '#B4EEEF');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Outros', NULL, 'help-circle', '#636E72');

INSERT INTO categories (name, parent_id, icon, color) VALUES 
('Não Categorizado', (SELECT id FROM categories WHERE name = 'Outros'), 'question', '#81868A'),
('Diversos', (SELECT id FROM categories WHERE name = 'Outros'), 'more-horizontal', '#9F9FA2');