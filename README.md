# SDG
Pet project for learning Java

# Backend Java-dev Q&A

## Java. Начало пути (_в никуда_)

**1. Перечислите методы класса `Object`**
>_С места в карьер. Начинаем с козырей. Этот вопрос перетечёт либо в обсуждение HashMap, либо в основы карательного многопоточного программирования_

- clone
- equals
- finalize (Deprecated)
- getClass
- hashCode
- toString
- notify
- notifyAll
- wait

**2. А расскажите про методы `wait`, `notify`, `notifyAll` и ключевое слово `synchronized`.**

Нет.

**3. JMM. Зачем нужно volatile. Популярный вопрос.**

http://www.javaspecialist.ru/2011/06/java-memory-model.html

**4.  Что можно положить и достать из List<? extends Number>, а что с List<? super Number>? Что такое ковариантность, контрвариантность, инвариантность?**

Тут речь про PECS — Producer extends, Consumer super (Joshua Bloch, Effective Java). А также вариантность — перенос наследования исходных типов на производные от них типы (контейнеры, делегаты, обобщения).

_**Ковариантность**_ (covariance) — перенос наследования исходных типов на производные от них типы в прямом порядке. Т.е. перенос с родителей на дочерние классы.
Переменной типа List<? extends **T**> разрешено присвоить экземпляр списка, параметризованного **T** или его подклассом, но не родительским классом. В него нельзя добавить никакой объект (можно только null) — нельзя гарантировать какого именно типа экземпляр списка будет присвоен переменной, поэтому нельзя гарантировать, что добавляемый объект разрешён в таком списке. Однако, из списка можно прочитать объект и он будет типа T и экземпляром либо T, либо одного из подклассов T.
Соответственно, List<? extends **Number**> можно присвоить ArrayList<**Number**> или ArrayList<**Integer**>, но не ArrayList<**Object**>. Метод get возвращает Number, за которым может скрываться экземпляр Integer или другого наследника Number.
Массивы также ковариантны.
Переопределение методов, начиная с Java 5, ковариантно относительно типа результата и исключений.

List<?> аналогичен List<? extends Object> со всеми вытекающими.

**_Контрвариантность_** (contravariance) — перенос наследования исходных типов на производные от них типы в обратном порядке. Т.е. для текущего типа переносится поведения родителя и вверх.
Переменной типа List<? super T> разрешено присвоить экземпляр списка, параметризованного T или его родительским классом, но не его подклассом. В список типа List<? super T> можно добавить экземпляр T или его подкласса, но нельзя добавить экземпляр родительских для T классов. Из такого списка с гарантией можно прочитать только Object, за которым может скрываться неизвестно какой его подкласс.
Соответственно, List<? super Number> можно присвоить либо ArrayList<Number>, либо ArrayList<Object>, но не список наследников Number (т.е. никаких ArrayList<Integer>). Можно добавить экземпляр Integer или Double (можно было бы Number, но он абстрактный), но нельзя — Object. Метод get возвращает Object — точнее сказать нельзя.

**_Инвариантность_** — наследование исходных типов не переносится на производные.
Переменной типа List<T> разрешено присвоить экземпляр списка, параметризованного только T. В список можно добавить экземпляр T или его подкласса. Список возвращает T, за которым может скрываться экземпляр его подкласса.
Соответственно, List<Number> можно присвоить ArrayList<Number>, но не ArrayList<Integer> или ArrayList<Object>. Можно добавить экземпляр Integer или Double (можно было бы Number, но он абстрактный), но нельзя — Object. Метод get возвращает Number, за которым может скрываться экземпляр Integer или другого наследника Number.

>Пиздец ебанутая история, без бутылки вискаря не разберёшься, в это нужно прям сесть и погрузиться. И переписать всю эту хуйню понятно и лаконично.

**5. В какой кодировке строки в Java? Как хранятся строки внутри класса String? Как устроен String?**

До Java 9 все строки имели кодировку UTF-16 (2 байта на символ) и хранились в массиве char.

С Java 9 пришло такое изменение как Compact String. Если все символы строки входят в множество символов Latin-1 (а это подавляющее большинство строк), то каждый из них может поместиться в 1 байт, поэтому в этом случае массив char избыточен. В результате было принято решение заменить массив char на массив byte, что позволяет строкам Latin-1 расходовать меньше памяти. Кодировка строки хранится в отдельном поле byte coder, значение которого представляет Latin-1 или UTF-16.

Также интересной особенностью является кеширование классом String своего hashcode.

Строки являются неизменяемыми, наследоваться от строк запрещено (final class). Все операции по изменении строки возвращают её новый экземпляр, в том числе и конкатенация строк. Компилятор умеет оптимизировать конкатенацию и превращать её в объект StringBuilder и совокупность вызовов методов append.  
ОДНАКО! В Java 9 вошёл JEP 280: Indify String Concatenation, который изменил эту оптимизацию и пошёл ещё дальше. Теперь вместо StringBuilder генерируется bytecode для вызова StringConcatFactory через invokedynamic, поэтому стоит расслабиться и чаще выбирать +.

Ещё можно упомянуть про String pool — это выделяемое в heap пространство, которое используется для оптимизации потребления памяти при хранении строк. Благодаря ему одинаковые строковые литералы могут ссылаться на один и тот же объект.

Кроме того, equals и методы поиска (например indexOf) оптимизируются JIT компилятором на нативном уровне.

>Посмотреть доклады Алексея Шипилёва на тему строк: [Катехизис java.lang.String](https://www.youtube.com/watch?v=SZFe3m1DV1A) и [The Lord of the Strings: Two Scours](https://www.youtube.com/watch?v=HWkVJkoo1_Q).

**6. Какие ссылки бывают в Java?**

Типы ссылок в Java:

- Strong reference — обычная переменная ссылочного типа в Java. Объект такой ссылки очищается GC не раньше, чем станет неиспользуемым (никто нигде на него больше не ссылается).

- Слабые ссылки — сборщик мусора тем или иным образом не учитывает связь ссылки и объекта в куче при выявлении объектов, подлежащих удалению. Объект будет удалён даже при наличии слабой ссылки на него:
  - Soft reference — мягкая ссылка, экземпляр класса SoftReference. Объект гарантированно будет собран GC до возникновения OutOfMemoryError. Может использоваться для реализации кэшей, увеличивающихся без риска OutOfMemoryError для приложения.
  - Weak reference — слабая ссылка, экземпляр класса WeakReference. Не препятствует утилизации объекта и игнорируется GC при сборке мусора. Может использоваться для хранения некоторой связанной с объектом информации до момента его смерти. Также стоит обратить внимание на WeakHashMap.
  - Phantom reference — фантомная ссылка, экземпляр класса PhantomReference. Не препятствует утилизации объекта и игнорируется GC при сборке мусора и имеет ряд особенностей, описанных ниже. Может быть применена для получения уведомления, что объект стал неиспользуемым и можно освободить связанные с ним ресурсы (как более надёжный вариант, чем finalize(), вызов которого не гарантируется, может проводить сеансы воскрешения и вообще deprecated).

Чтобы достать объект из слабых ссылок, необходимо вызывать метод get(). Если объект недостижим, то метод вернёт null. Для фантомных ссылок всегда возвращается null.

При создании слабой ссылки в конструктор можно, а для PhantomReference необходимо, передать экземпляр ReferenceQueue — в очереди будет сообщение, когда ссылка протухнет. Для SoftReference и WeakReference это будет ДО финализации объекта, а для PhantomReference ПОСЛЕ. Однако фактическое удаление объекта фантомной ссылки из памяти не производится до момента её очистки.

### Память и сборка мусора. Как работает? Какие сборщики? Какие области памяти в JVM? Что будет с двумя или более объектами, которые ссылаются только друг на друга, но больше не на кого и никому не нужны — как с ними поступит сборщик и как именно это будет делать?

Память в Java делится на Stack и Heap.  
_Stack_ — это область памяти, доступ к которой организован в порядке LIFO. Сюда помещается frame — локальные переменные и параметры вызываемого метода. Примитивы хранятся на стеке, у объектов тут хранится только ссылка, а сами объекты в Heap. (_но, благодаря Escape Analysis и скаляризации из Java 6, объекты, которые являются исключительно локальными и не возвращаются за пределы выполняемого метода, также сохраняются в стеке_)

Frame создаётся и кладётся на Stack при вызове метода. Frame уничтожается, когда завершается его вызов метода, как в случае нормального завершения, так и в результате выброса неперехваченного исключения. У каждого потока есть свой Stack и он имеет ограниченный размер.

Теперь про Heap и сборку мусора. Тут большинство просто хочет услышать — heap делится на два поколения:
- Young Generation
  - Eden
  - Survivor 0 и Survivor 1
- Old Generation
  - Tenured  

В Eden создаются все новые объекты. Один из Survivor регионов всегда пустой. При полном заполнении региона Eden запускается _малая сборка мусора_, и все живые объекты из Eden и Survivor перемещаются в пустой Survivor, а Eden и использующийся Survivor полностью очищается. Это делается для уменьшения фрагментации памяти. Объекты, которые несколько раз перемещаются между Survivor, затем помещаются в Tenured.

Когда места для новых объектов не хватает уже в Tenured, в дело вступает _полная сборка мусора_, работающая с объектами из обоих поколений. При этом старшее поколение не делится на подрегионы по аналогии с младшим, а представляет собой один большой кусок памяти. Поэтому после удаления мертвых объектов из Tenured производится не перенос данных (переносить уже некуда), а их уплотнение, то есть размещение последовательно, без фрагментации. Такой механизм очистки называется Mark-Sweep-Compact по названию его шагов (пометить выжившие объекты, очистить память от мертвых объектов, уплотнить выжившие объекты).

Бывают еще объекты-дегенераты-акселераты, размер которых настолько велик, что создавать их в Eden, а потом таскать за собой по Survivor’ам слишком накладно. В этом случае они размещаются сразу в Tenured.

_Младшее поколение занимает 1/3_ всей кучи, а _старшее 2/3_. При этом каждый регион _Survivor занимает 1/10_ младшего поколения, то есть Eden занимает 8/10.

Существуют следующие реализации сборщиков мусора:
- Serial Garbage Collector
- Parallel Garbage Collector. По умолчанию в Java 8.
- Concurrent Mark Sweep (CMS). Deprecated с Java 9.
- Garbage-First (G1). По умолчанию с Java 9.
- Z Garbage Collector (ZGC)
- Shenandoah Garbage Collector. Есть в наличии с Java 12.

Ещё можно вспомнить про:
- Method Area — область памяти с информацией о классах, включая статические поля. Одна на всю JVM.
- Program Counter (PC) Register — отдельный на каждый поток регистр для хранения адреса текущей выполняемой инструкции.
- Run-time Constant Pool — выделяется из Method Area для каждого класса или интерфейса. Грубо говоря, хранит литералы. Подробнее.
- Native Method Stack — собственно Stack для работы нативных методов.

**Могут ли быть в Java утечки памяти и когда? Как обнаружить причину? Как снять heap-dump?**

>Вот, блядь, чего никогда не встречал. Ну и хуй с ним, поехали, можжет могут спросить для галочки.  

Могут. Профилировать. Снимать heap-dump, например с помощью jmap, загружать в memory profiler (например в VisualVM).

### Коллекции (_которые, блядь, все обсасывают всегда со всех сторон, а на практике в 90% случаев пользуются ArrayList и не ебут голову ни себе ни разрабам_).

**1. Чем отличается ArrayList от LinkedList?**

_**ArrayList**_ это список, реализованный на основе массива, а _**LinkedList**_ — это связный список, основанный на объектах с ссылками между ними.   
_Преимущества ArrayList_: в возможности доступа к произвольному элементу по индексу за постоянное время, минимум накладных расходов при хранении такого списка.  
_Недостатки ArrayList_: вставка/удаление элемента в середину списка — взывает перезапись всех элементов размещенных «правее» в списке на одну позицию влево + при удалении элементов размер массива не уменьшается, до явного вызова метода trimToSize().

_**LinkedList**_ за постоянное время выполнять вставку/удаление элементов в списке (поиск позиции вставки и удаления сюда не входит). Доступ к произвольному элементу осуществляется за линейное время. LinkedList в абсолютных величинах проигрывает ArrayList по потребляемой памяти, по скорости выполнения операций. Предпочтительно применять, когда происходит активная работа (вставка/удаление) с серединой списка или когда необходимо гарантированное время добавления элемента в список.


**2. Что вы обычно используете (ArrayList или LinkedList)? Почему?**

>_Пиздец, блять, а ты мне в коде своём покажи где ты последний раз LinkedList использовал, псина? Умник хуев..._

В 90% случае ArrayList будет быстрее и экономичнее LinkedList, так что обычно используют ArrayList, но тем не менее всегда есть 10% случаев для LinkedList. Я говорю, что обычно ArrayList использую, ссылаясь на тесты и последний абзац из предыдущего вопроса, но не забываю и про LinkedList.

**3. Что быстрее работает ArrayList или LinkedList?**

Правильным будет встречный вопрос, какие действия будут выполняться над структурой? Вставка\удаление в середину списка — LinkedList, ходить по массиву одно за другим что-то с ним делать — ArrayList.

**4. Необходимо добавить 1млн. элемент, какую структуру вы используете?**

>_Если миллион хуёв, то жопу твоей жены._

Нужно задавать дополнительные вопросы: в какую часть списка происходит добавление? есть ли информация что потом будет происходить со списком? какие ограничения по памяти или скорости выполнения? Вы через дополнительные вопросы, показываете глубину понимания работы Array и Linked List.

**5. Как происходит удаление элементов из ArrayList? Как меняется в этом случае размер ArrayList?**

При удалении произвольного элемента из списка, все элементы находящиеся «правее» смещаются на одну ячейку влево и реальный размер массива (его емкость, capacity) не изменяется никак. Механизм автоматического «расширения» массива существует, а вот автоматического «сжатия» нет, можно только явно выполнить «сжатие» командой trimToSize().

**6. Предложите эффективный алгоритм удаления нескольких рядом стоящих элементов из середины списка, реализуемого ArrayList.**

Нужно удалить n элементов с позиции m в списке. Вместо выполнения удаления одного элемента n раз (каждый раз смещая на 1 позицию «правее»), нужно выполнить смещение всех элементов, стоящих «правее» n+m позиции на n элементов левее к началу списка. Вместо выполнения n итераций перемещения элементов, это выполняется за 1 проход.

**7. Как устроена HashMap?**
>_Это второй из списка самых популярных вопросов по коллекциям._

HashMap состоит из «корзин» (bucket). Корзины это элементы массива, которые хранят ссылки на списки элементов. При добавлении новой пары ключ-значение, вычисляется хеш-код ключа, на основании которого вычисляется номер корзины, куда попадет новый элемент. Если корзина пустая, то в нее сохраняется ссылка на вновь добавляемый элемент, если там уже есть элемент, то происходит последовательный переход по ссылкам между элементами в цепочке, в поисках последнего элемента, от которого и ставится ссылка на вновь добавленный элемент. Если в списке был найден элемент с таким же ключом, то он заменяется. 
Добавление, поиск и удаление элементов выполняется за константное время. Вроде все здорово, с одной оговоркой, хеш-функций должна равномерно распределять элементы по корзинам, в этом случае временная сложность для этих 3 операций будет не ниже **lg N**, а в среднем случае как раз константное время.

>_В целом, этого ответа вполне хватит на поставленный вопрос, дальше скорее всего завяжется диалог по HashMap, с углубленным пониманием процессов и тонкостей, которые нахуй никому не нужны, кроме долбоёбов и задротов_

**8. Роль equals и hashCode в HashMap?**

hashCode() позволяет определить корзину для поиска элемента, а equals() используется для сравнения ключей элементов в списке внутри корзины и искомого ключа.

**9. Максимальное число значений hashCode()?**

Вспомни сигнатуру метода: int hashCode(). Число значений равно диапазону int — 2^32.

**10. В каком случае может быть потерян элемент в HashMap?**

Допустим в качестве ключа используется объект с несколькими полями. После добавления элемента в HashMap у объекта-ключа изменяют одно поле, которое участвует в вычислении хеш-кода. В результате при попытке найти данный элемент по исходному ключу, будет происходить обращение к правильной корзине, а equals не найдет указанный ключ в списке элементов. Даже если equals реализован таким образом, что изменение данного поля объекта не влияет на результат, то после увеличения размера корзин и пересчета хеш-кодов элементов, указанный элемент, с измененным значением поля, с большой долей вероятности попадет совсем в другую корзину и тогда мы его точно проебём.

**11. Почему нельзя использовать byte[] в качестве ключа в HashMap?**

>_Потому что иди нахуй, вот почему_

Хеш-код массива не зависит от хранимых элементов, а присваивается при создании массива (метод вычисления хеш-кода массива вычисляется по Object.hashCode() на основании адреса массива). Так же у массивов не переопределен equals и выполняет сравнение указателей. Это приводит к тому, что обратиться к сохраненному с ключом-массивом элементу не получится при использовании другого массива такого же размера и с такими же элементами, доступ можно осуществить лишь в одном случае — при использовании той же самой ссылки на массив, что использовалась для сохранения элемента.

**12. В чем отличия TreeSet и HashSet?**

Set — это множество, не допускает хранение одинаковых элементов. Пояснение про хранение одинаковых элементов не требуется.  
**TreeSet** обеспечивает упорядоченно хранение элементов в виде красно-черного дерева. Сложность выполнения основных операций **lg N**.  
**HashSet** использует для хранения элементов такой же подход, что и HashMap, в HashSet в качестве ключа выступает сам элемент, HashSet (как и HashMap) не поддерживает упорядоченное хранение элементов и обеспечивает сложность выполнения операций аналогично HashMap.

**13. Устройство TreeSet?**

TreeSet основан на красно-черном дереве. Вот и всё. Идите нахуй.

**14. Что внутри и как работают TreeSet/TreeMap? В чем идея Красно-черного дерева?**
>Доёбистый техлид — горе в семье и траур в команде.

Внутри красно-чёрное дерево. Отъебитесь. Но если прям неймётся, то

`TreeMap` — реализация NavigableMap, основанная на красно-чёрном дереве. Элементы отсортированы по ключам в натуральном порядке или с помощью Comparator, указанного при создании мапы, в зависимости от использовавшегося конструктора. Гарантирует логарифмическое время выполнения методов `containsKey`, `get`, `put` и `remove`.

`TreeSet` — реализация NavigableSet, основанная на TreeMap. Элементы отсортированы в натуральном порядке или с помощью Comparator, указанного при создании множества, в зависимости от использовавшегося конструктора. Гарантирует логарифмическое время выполнения методов `add`, `contains` и `remove`.

Обе коллекции **НЕ** `synchronized` и итератор по ним может выбросить `ConcurrentModificationException`.

Если в эти коллекции при использовании натурального порядка сортировки в качестве ключа попытаться положить null, то получим NullPointerException. В случае с компаратором поведение с null будет зависеть от реализации компаратора.

Самая важная особенность красно-чёрного дерева в том, что **оно умеет само себя балансировать**, поэтому **не важно в каком порядке будут добавляться в него элементы**, преимущества этой структуры данных будут сохраняться. 

Сбалансированность достигается за счёт поддержания правил красно-чёрной раскраски вершин:

- Вершина может быть либо красной, либо чёрной и имеет двух потомков
- Красная вершина не может быть дочерней для красной вершины
- Количество чёрных вершин от корня до листа включительно одинаково для любого листа
- Корень дерева является чёрным
- Все листья — чёрные и не содержат данных

[Красно-черные деревья: коротко и ясно.](https://habr.com/ru/post/330644/)

**14. Что будет, если добавлять элементы в TreeSet по возрастанию?**

>Обычно данный вопрос предваряется твоей фразой №13, что в основе TreeSet лежит бинарное дерево и если добавлять элементы по возрастанию, то как они будут распределены по дереву.

Если есть общее понимание что такое бинарное дерево, то данный вопрос может привести к интересному результату: все элементы после добавления в обычное бинарное дерево будут находится в одной ветви длиной N, и пизда всем преимуществам структуры дерева (фактически получается список).  
На самом деле в основе TreeSet лежит красно-черное дерево, которое умеет само себя балансировать. В итоге, TreeSet`у поебать в каком порядке ты добавишь в него элементы, преимущества этой структуры данных он сохранит.

**15. Как работает ConcurrentHashMap?**  
`ConcurrentHashMap` — это потокобезопасная мапа (карта, словарь, ассоциативный массив, похуй как ты этот корабль назовёшь, но далее это будет просто "мапа"), у которой отсутствуют блокировки на всю мапу целиком.

Особенности реализации:
- Поля элемента мапы (Node<K,V>) val (значение) и next(следующее значение по данному ключу в цепочке или дереве), а также таблица бакетов (Node<K,V>[] table) объявлены как volatile;
- Для операций вставки первого элемента в бакет используется CAS — алгоритм, а для других операций обновления в этой корзине (insert, delete, replace) блокировки;
- Каждый бакет может блокироваться независимо путём блокировки первого элемента в корзине;
- Таблице бакетов требуется volatile/atomic чтения, запись и CAS, поэтому используются intrinsics-операции (jdk.internal.misc.Unsafe);
- Concurrent resizing таблицы бакетов;
- Ленивая инициализация таблицы бакетов;
- При подсчёте количества элементов используется специальная реализация LongAdder.

В результате имеем:
- Извлечение значения возвращает последний результат завершенного обновления мапы на момент начала извлечения. Или перефразируя, любой non-null результат, возвращаемый get(key) связан отношением happens-before со вставкой или обновлением по этому ключу;
- Итераторы по ConcurrentHashMap возвращают элементы отображающие состояние мапы на определённый момент времени — они не бросают ConcurrentModificationException, но предназначены для использования одним потоком одновременно;
- Нельзя полагаться на точность агрегирующих методов (size, isEmpty, containsValue), если мапа подвергается изменениям в разных потоках;
- Не позволяет использовать null, который однозначно воспринимается как отсутствие значения;
- Поддерживает потокобезопасные, затрагивающие все (или многие) элементы мапы, операции — forEach, search, reduce (bulk operations). Данные операции принимают на вход функции, которые не должны полагаться на какой-либо порядок элементов в мапе и в идеале должны быть чистыми (за исключением forEach). На вход данные операции также принимают parallelismThreshold — операции будут выполняться последовательно, если текущий размер мапы меньше parallelismThreshold. Значение Long.MAX_VALUE сделает операцию точно последовательной. Значение 1 максимизирует параллелизм и утилизацию ForkJoinPool.commonPool, который будет использоваться для параллельных вычислений.

Более подробно про  всю эту историю можно найти [тут](https://habr.com/ru/post/327186/).

### Многопоточность (_ёб это блять мать в рот и жопу_)

**1. Что такое Executor и ExecutorService, Thread pool и зачем нужны?**

Всё это какая-то хуйня из-под коня, но вот немного инфы: создавать и убивать потоки — дорого. Давайте ебанём N потоков (Thread pool) и будем их переиспользовать. А давайте.

`Executor` (void execute(Runnable command) — вот и весь интерфейс) и `ExecutorService` (уже покруче, может запускать Callable и не только) — грубо говоря, интерфейсы выполняторов параллельных задач. А реализуют их различные выполняторы на пулах потоков. Экземпляры готовых конкретных выполняторов можно получить с помощью класса `Executors`. Если смелый-ловкий-и-умелый и зачем-то надо, то можно и самому реализовать, конечно.

**2. Что внутри параллельных стримов? На каком пуле работают параллельные стримы и в чем его особенность?**

**3. Как работает ConcurrentHashMap?**
см.п.15 коллекций.

**4. Как работают Атомики?**

Атомарная операция — это операция, которая выполняется полностью или не выполняется совсем, частичное выполнение невозможно.
Атомики — это классы, которые выполняют операции изменения своего значения атомарно, т.о. они поддерживают lock-free thread-safe использование переменных. Достигается это с помощью алгоритма compare-and-swap (CAS). На уровне инструкций большинства процессоров имеется поддержка CAS.

В общем случае работу Атомиков можно описать следующим образом: атомик хранит некоторое volatile значение value, для изменения которого используется метод compareAndSet(current, new), поэтому предварительно читается текущее значение — current. Данный метод с помощью CAS изменяет значение value только в том случае, если оно равно ожидаемому значению (т.е. current), прочитанному перед запуском compareAndSet(current, new). Если значение value было изменено в другом потоке, то оно не будет равно ожидаемому. Следовательно, метод compareAndSet вернет значение false. Поэтому следует повторять попытки чтения текущего значения и запуска с ним метода compareAndSet(current, new) пока current не будет равен value.

Условно можно разделить методы Атомиков на:
- compare-and-set — принимают current на вход и делают одну попытку записи через CAS
- set-and-get — самостоятельно читают current и пытаются изменить значение с помощью CAS в цикле, как описано выше

Непосредственно изменение значения value делегируется либо VarHandle, либо Unsafe, которые в свою очередь выполняют его на нативном уровне. VarHandle — это динамически сильно типизированная ссылка на переменную или на параметрически определяемое семейство переменных, включающее статические поля, нестатические поля, элементы массива или компоненты структуры данных нестандартного типа. Доступ к таким переменным поддерживается в различных режимах, включая простой доступ на чтение/запись, volotile доступ на чтение/запись и доступ на compare-and-swap.

В java.util.concurrent.atomic имеется следующий набор атомиков:
- AtomicBoolean, AtomicInteger, AtomicLong, AtomicIntegerArray, AtomicLongArray — представляют атомарные целочисленные, булевы примитивные типы, а также два массива атомарных целых чисел.
- AtomicReference — класс для атомарных операций со ссылкой на объект.
- AtomicMarkableReference — класс для атомарных операций над парой [reference, boolean].
- AtomicStampedReference — класс для атомарных операций над парой [reference, int].
- AtomicReferenceArray — массив атомарных ссылок
- AtomicIntegerFieldUpdater, AtomicLongFieldUpdater, AtomicReferenceFieldUpdater — классы для атомарного обновления полей по их именам через reflection.
- DoubleAccumulator, LongAccumulator — классы, представляющие атомарные аккумуляторы, которые принимают на вход чистую функцию-аккумулятор (BinaryOperator) и начальное значение. Сохраняет весь набор операндов, а когда необходимо получить значение, то аккумулирует их с помощью функции-аккумулятора. Порядок операндов и применения функции-аккумулятора не гарантируется. Используется, когда записей намного больше, чем чтения.
- DoubleAdder, LongAdder — классы, представляющие атомарные счётчики. Являются частным случаем атомарных аккумуляторов, у которых функция-аккумулятор выполняет простое суммирование, а начальным значением является 0.

[Если ты смелый-ловкий-и-умелый, читай «Java Concurrency на практике».](https://habr.com/ru/company/piter/blog/489038)
### Stream API
**1. Какие бывают операции в стримах? Напишите стрим?**

Есть 2 вида операций в Java Stream:
- Промежуточные (Intermediate) — filter, map, sorted, peek и т.д. Возвращают Stream.
- Терминальные (Terminal) — collect, forEach, count, reduce, findFirst, anyMatch и т.д. Возвращают результат стрима и запускают его выполнение.

Кроме того, будет полезно ознакомиться с содержимым пакета java.util.stream и доступными коллекторами из Collectors.
Написать стрим? Иди на хуй, покажи в кодбазе проекта где ты там как стримы использовал и не еби мозг тупыми вопросами, умник хуев.

# Всё. "Начало пути" окончено, котик. Ебашим во фреймворки (_помоги нам господь, дева мария, все святые и праведные_)

## Spring, блядь, аллилуйа его мать, framework. Понеслась пизда по кочкам!

>Сразу же мастхэв, который даже я на момент написания не досмотрел — Евгений Борисов — Spring-потрошитель, [часть 1](https://www.youtube.com/watch?v=BmBr5diz8WA&t=2323s), [часть 2](https://www.youtube.com/watch?v=cou_qomYLNU), [Spring Patterns](https://www.youtube.com/watch?v=61duchvKI6o), [Spring-построитель](https://www.youtube.com/watch?v=rd6wxPzXQvo), [Spring Patterns для взрослых](https://www.youtube.com/watch?v=GL1txFxswHA). Просмотришь это — будешь на протяжении всего интервью в галеру своей мечты всех техлидов ебать и в хвост, и в гриву за Spring и прочую хуйню.

**1. 

## Ссылки на все материалы. Для особенно пытливых.  

**Общее**

[Собеседование Backend-Java-разработчика: вопросы и где искать ответы. Часть 1](https://habr.com/ru/post/529210/)  

[Собеседование Backend-Java-разработчика: вопросы и где искать ответы. Часть 2](https://habr.com/ru/post/529214/)

**Стримы**

[Шпаргалка Java программиста 4. Java Stream API](https://habr.com/ru/company/luxoft/blog/270383/)

[Полное руководство по Java 8 Stream API в картинках и примерах](https://annimon.com/article/2778)

[]()
[]()

**Generics \ Дженерики**

[Пришел, увидел, обобщил: погружаемся в Java Generics](https://habr.com/ru/company/sberbank/blog/416413/)

[Использование generic wildcards для повышения удобства Java API](https://habr.com/ru/post/207360/)

[Вариантность в программировании](https://habr.com/ru/post/218753/)
