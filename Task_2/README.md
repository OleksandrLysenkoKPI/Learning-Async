# Практичне заняття 2

**Варіант 1.** Обчислення факторіалів\
Створіть ConcurrentHashMap, де ключами є числа, а значеннями — факторіали цих чисел. Створіть кілька Callable, які паралельно обчислюють
факторіали чисел і зберігають результати в мапі.\
Використовуйте Future, щоб зібрати та вивести результати обчислень. У задачі необхідно використати метод isCancelled().