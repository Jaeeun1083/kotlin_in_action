# 4. 클래스, 객체, 인터페이스

## 1. 클래스 계층 정의

### 인터페이스

코틀린의 인터페이스는 자바 8 인터페이스와 비슷하다.
추상 메소드뿐 아니라, 구현이 있는 메소드도 정의할 수 있다.(자바 8의 디폴트 메소드와 비슷하다.)
다만, 인터페이스에는 아무런 상태(필드)도 들어갈 수 없다.

아래와 같이 추상 메소드를 정의할 경우 인터페이스를 구현하는 모든 비추상 클래스(구체 클래스)는 `click` 함수에 대한 구현을 제공해야 한다.

```kotlin
interface Clickable {
    fun click()
}
```

```kotlin
class Button : Clickable {
    override fun click() = println("I was clicked")
}
```

반면 `showOff` 메소드의 경우 새로운 동작을 정의할 수도 있고, 정의를 생략해서 default 구현을 사용할 수도 있다.

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable")
}
```

```kotlin
interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")

    fun showOff() = println("I'm focusable")
}
```

만약 위 두 인터페이스를 구현한 클래스가 있고, `showOff()`를 호출하면 어떤 인터페이스의 default 메소드가 실행될까?
인텔리제이를 통해 `Implement members`를 실행하면, 아래와 같이 동일한 메소드가 생긴다.

```kotlin
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")
    override fun showOff() {
        TODO("Not yet implemented")
    }
    override fun showOff() {
        TODO("Not yet implemented")
    }
}
```

클래스가 구현하는 두 상위 인터페이스에 정의된 `showOff`를 구현을 대체할 오버라이딩 메솓를 직접 제공하지 않으면, 컴파일 오류가 발생한다.

```
Class 'Button' must override public open fun showOff(): Unit
defined in chapter04.part1.Clickable 
because it inherits multiple interface methods of it
```

코틀린 컴파일러는 두 메소드를 아우르는 구현을 하위 클래스에 직접 구현하게 강제한다.
때문에 아래 코드와 같이 `super`를 지정해 상위 타입의 이름을 꺾쇠 사이에 넣으면, 어떤 상위 타입의 메소드를 호출할지 지정할 수 있다.

```kotlin
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")
    override fun showOff() {
        super<Focusable>.showOff()
        super<Clickable>.showOff()
    }
}
```

#### 콜론(:) 컨벤션

코틀린 [공식 문서](https://kotlinlang.org/docs/coding-conventions.html#colon)에 나와있듯,
클래스의 상속, 구현은 ` : `를 사용하며, 변수 및 함수의 리턴 타입은 `: `를 사용한다.

```kotlin
abstract class Foo<out T : Any> : IFoo {
    abstract fun foo(a: Int): T
}

class FooImpl : Foo() {
    constructor(x: String) : this(x) { /*...*/ }
    val x = object : IFoo { /*...*/ }
}

val mutableCollection: MutableSet<String> = HashSet()
```

### open, final, abstract 변경자

#### open, final

자바에서 `final`을 명시한 클래스는 상속을 할 수 없다.
기본적으로 상속이 가능하면, 편리한 경우도 있지만, 취약한 기반 클래스(fragile base class)라는 문제가 생긴다.
즉, 하위 클래스가 기반 클래스에서 구현한 코드를 변경함으로써 문제가 생기는 것이다.

모든 하위 클래스를 분석하는 것은 불가능하므로, 기반 클래스를 변경하는 경우 하위 클래스의 동작이 예기치 않게 바뀔 수도 있다는 면에서 기반 클래스는 취약하다.

[Effective Java, Item19;상속을 고려해 설계하고, 문서화하라. 그렇지 않았다면 상속을 금지하라.]에 나오는 글을 보면, 다음과 같은 글이 등장한다.

> 문서화를 해놓지 않은 '외부' 클래스를 상속할 때의 위험을 경고했다. 여기서 '외부'란, 프로그래머의 통제권 밖에 있어서 언제 어떻게 변경될지 모른다는 뜻이다.
> 상속용 클래스는 재정의할 수 있는 메소드들을 내부적으로 어떻게 이용하는지(자기 사용) 문서로 남겨야 한다.

코틀린도 위 철학을 따라, 기본 클래스와 메소드는 `final`로 되어 상속을 금지하고 있다.
때문에 상속을 허용하려면 `open` 변경자를 붙여야하며, `override`를 해줄 함수에도 `open`을 붙여야 한다.

```kotlin
// 다른 클래스가 이 클래스를 상속할 수 있다.
open class RichButton : Clickable {
    // 상위 클래스에서 선언된 open 메소드를 오버라이드 했다.
    // 오버라이드한 메소드는 기본적으로 열려있다.
    override fun click() { }
    // final 함수로 지정되어 있어 오버라이드할 수 없다.
    fun disable() { }
    // 이 함수는 열려있어 하위 클래스에서 오버라이드할 수 있다.
    open fun animate() { }
}
```

만약 `click()`과 같이 상위에서 받아온 메소드에 대한 구현을 금지하고 싶다면 `final`을 명시해야 한다.

```kotlin
open class RichButton : Clickable {
    // 하위 클래스에서 오버라이드할 수 없다.
    final override fun click() { }
}
```

#### abstract

`abstract` 클래스의 특징은 다음과 같다.

- 추상 클래스는 인스턴스화할 수 없다.
- 구현이 없는 추상 멤버가 존재할 수 있다.
  - 하위 클래스에서는 그 추상 멤버를 오버라이드 해야만 하는게 보통이다.
- 추상 멤버는 항상 열려 있어 `open` 변경자를 명시하지 않아도 된다.

```kotlin
abstract class Animated {
    // 추상 함수 : 구현이 없으며, 하위 클래스에서 반드시 오버라이드 해야 함
    abstract fun animate()
    
    // 비추상 함수는 기본적으로 final임
    fun animateTwice() { }
    
    // open 변경자를 통해 오버라이드를 허용할 수 있음
    open fun stopAnimating() { }
}
```

| 변경자      | 특징                          | 설명                                 |
|----------|-----------------------------|------------------------------------|
| final    | 오버라이드할 수 없음                 | 클래스 멤버의 기본 변경자                     |
| open     | 오버라이드할 수 있음                 | 반드시 open을 명시해야 오버라이드할 수 있음         |
| abstract | 반드시 오버라이드 해야 함              | 추상 클래스의 멤버에만 붙을 수 있으며, 구현이 있으면 안 됌 |
| override | 상위 클래스나 상위 인터페이스의 멤버를 오버라이드 | 하위 클래스의 오버라이드를 막으려면 final을 명시해야 함  |

### 가시성 변경자

코틀린의 기본 가시성(아무것도 명시하지 않을 경우)은 `public`이다.
패키지를 네임스페이스(namespace)를 관리하기 위한 용도로만 사용하기 때문에 가시성 제어에 사용하지 않는다.

코틀린에는 `internal` 이라는 새로운 가시성 변경자가 있는데, 이는 '모듈 내부에서만 볼 수 있음'이라는 의미로 해석된다.
모듈은 한꺼번에 컴파일되는 코틀린 파일을 의미한다.

- 인텔리제이, 메이븐, 그레이들 등의 프로젝트
- 앤트 태스크가 한 번 실행될 때 함께 컴파일되는 파일의 집합

| 변경자       | 클래스 멤버              | 최상위 선언             |
|-----------|---------------------|--------------------|
| public    | 모든 곳에서 볼 수 있다.      | 모든 곳에서 볼 수 있다.     |
| internal  | 같은 모듈 안에서만 볼 수 있다.  | 같은 모듈 안에서만 볼 수 있다. |
| protected | 하위 클래스 안에서만 볼 수 있다. | (최상위 선언에 적용할 수 없음) |
| private   | 같은 클래스 안에서만 볼 수 있다. | 같은 파일 안에서만 볼 수 있다. |

위 가시성 규칙을 위반하는 코드를 통해 살펴보자.

```kotlin
internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

fun TalkativeButton.giveSpeech() {
    yell()
    whisper()
}
```

위 코드를 작성하면 두 가지 에러가 발생하는 것을 볼 수 있다.

첫 번째는 확장 함수 선언에 대한 에러이다.
`Talkative`는 internal 가시성으로 지정되어 있다.
하지만 확장 함수는 기본 가시성인 public으로 선언되어, 그보다 가시성이 더 낮은 internal을 참조하지 못한다.

두 번째는 확장 함수 내부에서 호출하는 함수이다.
`yell()`의 경우 private 가시성을 갖고 있기 때문에 클래스 내부에서만 호출할 수 있다.
확장 함수가 마치 클래스 내부에 선언된 함수처럼 사용할 수 있다고 하더라도,
선언 자체는 클래스 외부에서 했기 때문에 private 가시성을 가진 함수를 호출할 수 없다.
`whisper()`의 경우에도 protected 가시성을 갖고 있어, `Talkative` 클래스 내부 혹은 이를 상속한 클래스에서만 사용할 수 있다.

> internal은 자바와 딱 맞는 가시성이 없기 때문에 바이트 코드 상에서 public으로 나타나게 된다.

### 중첩(nested) 클래스

코틀린의 중첩 클래스는 명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다.
왜 이런 특성이 중요한지 코드를 통해 살펴보자.

아래 코드는 View 요소를 만들 때, 상태를 직렬화하기 위한 코드이다.
이를 위해, State 인터페이스를 선언하고, Serializable을 구현하도록 한다.

```kotlin
interface State : Serializable
interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) { }
}
```

이제 뷰에서 사용되는 버튼을 자바 클래스로 만들어보자.

```java
public class Button implements View {

    @NotNull
    @Override
    public State getCurrentState() {
        return new ButtonState();
    }

    @Override
    public void restoreState(@NotNull State state) { }

    public class ButtonState implements State { 
        public boolean status = false;
    }
}
```

`State`를 구현한 `ButtonState` 클래스를 정의한 뒤, 버튼에 대한 정보를 저장한다.
`getCurrentState`를 통해서 현재 상태에 대한 인스턴스를 반환한다.

하지만 위 코드에서 `ButtonState`는 바깥 클래스인 `Button` 클래스에 대한 참조를 묵시적으로 포함하므로, 직렬화할 수 없다.
이 문제를 해결하기 위해서는 `ButtonState`를 `static` 클래스로 선언해야 바깥쪽 클래스에 대한 묵시적인 참조가 사라져 직렬화할 수 있게 된다.

코틀린에서 중첩 클래스가 동작하는 방식은 이와 정반대이다.

```kotlin
class _Button : View {
    override fun getCurrentState(): State { }
    override fun restoreState(state: State) { }
    class ButtonState : State { }
}
```

코틀린에서 중첩 클래스에 아무런 변경자가 붙지 않으면, 자바 `static` 중첩 클래스와 동일하다.
만약 내부 클래스로 변경하려면 `inner` 변경자를 붙여야 한다.

```kotlin
class Outer {
    // inner 변경자를 붙이지 않으면 this@Outer에서 에러가 발생함
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}
```

| 구분                         | 자바            | 코틀린           |
|----------------------------|---------------|---------------|
| 중첩 클래스(바깥 클래스에 대한 참조 저장 X) | static class A | class A       |
| 내부 클래스(바깥 클래스에 대한 참조 저장 O) | class A       | inner class A |

### 봉인된(sealed) 클래스

코틀린 컴파일러는 `when`을 사용할 경우 `else` 분기를 붙이도록 강제한다.
때문에 반환할 만한 의미 있는 값이 없으므로 보통 예외를 던지기 마련이다.
예외 분기를 처리하는 것은 편하지않고, 버그를 발생시킬 수 있는 원인이 되기도 한다.

`sealed` 변경자를 붙이면 이러한 문제를 해결할 수 있다. 

```kotlin
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

// Expr의 하위 클래스는 Num, Sum 뿐이기 때문에 별도의 else를 처리해줄 필요가 없다.
fun eval(e: Expr): Int = when (e) {
    is Expr.Num -> e.value
    is Expr.Sum -> eval(e.right) + eval(e.left)
}
```

`sealed` 클래스는 기본적으로 `open`이며, 하위 클래스를 정의할 때는 반드시 상위 클래스 안에 중첩시켜야 한다.
그러면, 그 상위 클래스를 상속한 하위 클래스 정의를 제한할 수 있다.
또한, 하위 클래스가 새로 생길 경우, `when`에 해당 분기를 추가하도록 경고를 내뿜는다.

```
'when' expression must be exhaustive, add necessary 'is Div' branch or 'else' branch instead
```

## 2. 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언

코틀린은 주생성자(primary constructor; 클래스를 초기화할 때 주로 사용하는 간략한 생성자로, 클래스 본문 밖에서 정의함)
와 부생성자(secondary constructor; 클래스 본문 안에서 정의함)를 구분하며, 초기화 블록(initializer block)을 통해 초기화 로직을 추가할 수 있다.

### 클래스 초기화 : 주 생성자와 초기화 블록

주 생성자(primary constructor)는 두 가지 목적으로 사용된다.

1. 생성자 파라미터를 지정
2. 생성자 파라미터에 의해 초기화되는 프로퍼티 정의

```kotlin
class User constructor(_nickname: String) {
    val nickname: String
    init {
        nickname = _nickname
    }
}
```

위 코드에 등장하는 키워드는 다음과 같다.

- constructor : 주 생성자나 부 생성자 정의를 시작할 때 사용
  - 애노테이션이나 가시성 변경자가 없다면 생략 가능
- init : 초기화 블록; 클래스의 객체가 만들어질 때(인스턴스화될 때) 실행
  - 주 생성자와 함께 사용
  - 필요에 따라 여러 초기화 블록을 선언 가능

위 코드를 조금 더 깔끔하게 만들면 다음과 같다.

```kotlin
class User constructor(_nickname: String) { 
    val nickname: String = _nickname 
}
```

```kotlin
class User (val nickname: String)
```

위와 같이 생성자 파라미터에 val을 넣어줄 경우 그에 상응하는 프로퍼티가 생성된다.
또한, 함수와 마찬가지로 생성자 파라미터에도 디폴트 값을 정의할 수 있다.

```kotlin
class User (val nickname: String, val isSubscribed: Boolean = false)
```

클래스에 기반 클래스가 있다면, 다음과 같이 초기화할 수 있다.

```kotlin
open class User (val nickname: String)

class TwitterUser(nickname: String) : User(nickname)
```

만약 기반 클래스에 생성자가 없을 경우 자바와 동일하게 컴파일러가 비어있는 디폴트 생성자를 만들어준다.

```kotlin
open class User
class TwitterUser(nickname: String) : User()
```

> 클래스의 경우 생성자가 있어 기반 클래스 이름 뒤에 소괄호가 붙지만,
> 인터페이스의 경우 인스턴스화를 할 수 없기 때문에 소괄호가 붙지 않는다.

어떤 클래스를 외부에서 인스턴스화하지 못하게 막고 싶다면 생성자를 private으로 만들면 된다.

```kotlin
class Secretive private constructor() {}
```

위와 같이 작성하면, 외부에서 `Secretive`를 인스턴스화하지 못하게 되어 싱글톤 클래스로 활용할 수 있다.

### 부 생성자 : 상위 클래스를 다른 방식으로 초기화

아래 코드는 주 생성자를 선언하지 않고, 부 생성자만 선언햇다.

```kotlin
open class View {
    constructor(ctx: Context)
    constructor(ctx: Context, attr: AttributeSet)
}
```

위 클래스의 하위 클래스에서는 `View`에 대한 인스턴스를 만들기 위해 `super`로 상위 클래스 생성자를 호출할 수 있다.
이러한 방식으로 생성자가 상위 클래스 생성자에게 객체 생성을 위임할 수 있다.

```kotlin
class MyButton : View {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr)
}
```

`super`가 아닌 `this`를 사용해서 자신의 다른 생성자를 호출할 수도 있다.

```kotlin
class MyButton : View {
  constructor(ctx: Context) : this(ctx, MY_STYLE)
  constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr)
}
```

### 인터페이스에 선언된 프로퍼티 구현

인터페이스에는 추상 프로퍼티를 선언할 수 있다.
해당 인터페이스의 구현체에서 `nickname` 프로퍼티를 사용하려면, 먼저 초기화를 해주어야 한다.

```kotlin
interface Member {
    val nickname: String
}
```

첫 번째 방식은 주 생성자를 통해 프로퍼티를 바로 선언하는 것이다.
이러한 방식을 사용할 경우 `PrivateMember` 객체를 생성함과 동시에 `nickname`에 대한 값을 넣어줄 수 있다.

```kotlin
class PrivateMember(override val nickname: String) : Member
```

두 번째 방식은 커스텀 게터를 사용해 프로퍼티를 설정했다.
Backing field에 값을 저장하지 않고, 매번 이메일 주소에서 별명을 계산해 반환하는 방식이다.

```kotlin
class SubscribingMember(val email: String) : Member {
    override val nickname: String
        get() = email.substringBefore('@')
}
```

마지막 방식은 함수를 통해 `nickname`을 초기화하는 방식이다.

> `getFacebookName`은 페이스북에 접속해서 인증을 거친 후 원하는 데이터를 가져와야 하기 때문에 비용이
> 많이 들 수도 있다. 그래서 객체를 초기화하는 단계에 한 번만 `getFacebookName`을 호출하게 설계했다.

```kotlin
class FacebookMember(val accountId: Int) : Member {
    override val nickname: String = getFacebookName(accountId)
}
```

### 게터와 세터에서 뒷받침하는 필드에 접근

앞서 본 것과 같이 프로퍼티에는 값을 저장하는 프로퍼티와 커스텀 접근자를 통해 매번 값을 계산하는 프로퍼티가 존재한다.
아래 코드는 두 유형을 조합한 코드이다.

```kotlin
class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("""
                Address was changed for $name:
                "$field" -> "$value"
            """.trimIndent())
            field = value
        }
}
```

위 코드에서 `field`는 뒷받침하는 필드(Backing field)라고 부른다.

```kotlin
fun main() { 
    val user = User("Alice")
    user.address = "Elsenheimerstrasse 47, 80687 Muenchen"
}
```

`address`에 값을 대입하게 될 경우 `setter`를 호출하게 되는데,
만약 User 클래스의 `field = value` 부분을 `address = value`로 변경할 경우 계속해서 setter를 호출해 무한 순환 참조가 일어나게 된다.

### 커스텀 접근자의 가시성 변경

Java Spring에서 개발을 할 때, 금기 시 되는 것 중 하나가 바로 Entity class에서 Setter를 사용하는 것이다.
이는 외부에서 데이터를 접근해 잘못 수정될 수 있기 때문이다.
하지만 코틀린에서는 기본적으로 Setter가 없어도 `field = value`를 통해 값을 대입할 수 있다.
이를 막기 위해 아래 코드처럼 set의 가시성을 제한할 수 있다.

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set

    fun addWord(word: String) {
        counter += word.length
    }
}
```

```kotlin
fun main() {
    val cnt = LengthCounter()
    // 에러 발생 : cannot assign to 'counter': the setter is private in 'LengthCounter'
    cnt.counter = 10
}
```



## 3. 컴파일러가 생성한 메소드 : 데이터 클래스와 클래스 위임

자바 플랫폼에서는 기본적으로 equals, hashCode, toString 등의 메소드를 직접 구현해야 객체간의 동등성을 쉽게 비교할 수 있게 된다.
하지만 코틀린 컴파일러는 이런 메소드를 보이지 않는 곳에서 생성해준다.

### 모든 클래스가 정의해야 하는 메소드

자바와 마찬가지로 코틀린에서도 equals, hashCode, toString를 오버라이드할 수 있다.

```kotlin
class Client(val name: String, val postalCode: Int)
```

위 클래스에 대한 객체를 출력하면 다음과 같은 결과가 나온다.

```
package.Client@6e2c634b
```

이러한 정보는 사용하기 어렵기 때문에 클래스가 갖고 있는 정보들을 출력해주도록 재구현해야한다.

```
class Client(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
}
```

위와 같이 Any 클래스에 있는 `toString`을 오버라이딩해 재구현할 수 있다.

#### 객체의 동등성 : equals()

아래 두 객체는 주소값은 다르지만, 완전히 동일한 값을 담고 있는 객체이다.

```kotlin
fun main() {
    val client1 = Client("Alice", 4122)
    val client2 = Client("Alice", 4122)
    // 출력 : false
    println(client1 == client2)
}
```

> 자바에서 == 연산은 원시 타입과 참조 타입의 주소값을 비교할 때 사용한다.
> 코틀린에서의 == 연산은 내부적으로 equals를 호출해 객체를 비교하기 때문에 equals를 오버라이드를 하면 ==을 통해 클래스의 인스턴스를 비교할 수 있다.
> 또한, ===을 사용하면 자바의 == 연산으로 사용할 수 있게 된다.

출력 결과와 같이 정상적으로 `false`를 반환하는 것을 볼 수 있다.
하지만 요구 사항에 따라 서로 다른 두 객체가 내부에 동일한 데이터를 포함할 경우 그 둘을 동등한 객체로 간주해야할 때가 있다.

```kotlin
class Client(val name: String, val postalCode: Int) {
    override fun equals(other: Any?): Boolean {
        // is는 자바의 instanceof 연산자와 같다.
        if(other == null || other !is Client) return false
        // 위에서 Client에 대한 객체인지 검사를 했기 때문에 스마트 캐스팅이 된다.
        return name == other.name && postalCode == other.postalCode
    }
}
```

코틀린에서는 `override` 변경자가 필수이기 때문에 `other:Any?`를 `other:Client`로 작성할 수 없다.
때문에 해당 함수를 오버라이드하고 나면 프로퍼티의 값이 모두 같은 두 고객 객체는 동등하리라 예상할 수 있다.

#### 해시 컨테이너 : hashCode()

`equals`를 재정의할 때, `hashCode`도 함께 오버라이드해야 한다. 그 이유는 다음 코드를 보면 알 수 있다.

```kotlin
fun main() {
    val processed = hashSetOf(Client("Alice", 4122)) 
    // 출력 : false
    println(processed.contains(Client("Alice", 4122)))
}
```

이름이 Alice인 클라이언트를 생성해 `HashSet`에 넣어주었고, 해당 `Set`에 동일한 클라이언트 객체를 생성해 포함되어있는지 확인해보면 `false`가 나오는 것을 볼 수 있다.
`toString` 오버라이드를 하지 않은 상태로 두 객체를 출력해보면 다음과 같은 해시값을 가지고 있는 것을 볼 수 있다.

```
package.Client@37a71e93
package.Client@6e2c634b
```

즉, 두 객체의 서로 다른 해시값을 가지고 있기 때문에 `HashSet`에서 동일한 객체로 판별하지 않고, `equals` 또한 판단 결과에 영향을 끼치지 못한다.
이를 해결하기 위해서는 클라이언트 클래스 내에 `hashCode` 함수를 재정의 해주어야 한다.

```kotlin
class Client(val name: String, val postalCode: Int) { 
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
    ...
}
```

`hashCode`를 정의하는 규약은 [링크](https://github.com/Jwhyee/effective-java-study/blob/574ad6188fa07f6957ebd43281fc1bcfc04cd8e8/src/main/java/ka/chapter3/item11/item11.md#hashcode%EC%9D%98-%EC%9D%BC%EB%B0%98-%EA%B7%9C%EC%95%BD)를 통해 확인할 수 있다.

### 데이터 클래스 : 모든 클래스가 정의해야 하는 메소드 자동 생성

어떤 클래스가 데이터를 저장하는 역할만을 수행한다면 `toString`, `equals`, `hashCode`를 반드시 오버라이드해야 한다.
코틀린에는 `data`라는 변경자를 클래스 앞에 붙이면 위 메소드들을 컴파일러가 자동으로 생성해준다.

```kotlin
data class Client(val name: String, val postalCode: Int)
```

위 코드를 자바 코드로 디컴파일 해보면 final class로 되어있는 것을 볼 수 있다.
즉, data class는 데이터를 저장하는 역할로만 사용하는 클래스이므로, 다른 클래스에 상속할 수 없다.

#### 데이터 클래스와 불변성 : copy()

앞서 언급한 것처럼 data class는 데이터를 저장하는 용도로만 사용하기에 가능한 모든 프로퍼티를 읽기 전용(read only)으로 만들어 불변(immutable) 클래스로 만들라고 권장한다.

값을 수정해야할 경우에는 객체를 복사(copy)해 원본과 다른 생명주기를 가지게 할 수 있다.
복사본을 제거해도 프로그램에서 원본을 참조하는 다른 부분에 전혀 영향을 끼치지 않는다.

```kotlin
// 데이터 클래스일 경우
data class Client(val name: String, val postalCode: Int)

// 일반 클래스일 경우
class Client(val name: String, val postalCode: Int) { 
    fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Client(name, postalCode)
}

fun main() {
    val origin = Client("Alice", 4122)
    val copy = origin.copy("Alice", 4222)
}
```

### 클래스 위임 : by 키워드 사용

상속을 허용하지 않는 클래스에 새로운 동작을 추가해야할 때, 데코레이터 패턴을 사용한다.

> 데코레이터 패턴이란, 상속을 허용하지 않는 클래스 대신 사용할 수 있는 새로운 클래스(데코레이터)를 만들되 기존 클래스와 같은 인터페이스를 데코레이터가 제공하게 만들고, 기존 클래스를 데코레이터 내부에 필드로 유지하는 것
> 이때 새로 정의해야하는 기능은 데코레이터의 메소드에 새로 정의하고, 기존 기능이 그대로 필요한 부분은 데코레이터의 메소드가 기존 클래스의 메소드에게 요청을 전달한다.

이러한 설계의 단점은 아래와 같이 복잡한 코드를 작성해야하는 것이다.

```kotlin
class DelegatingCollection<T> : Collection<T> {
  private val innerList = arrayListOf<T>()
  override val size: Int get () = innerList.size
  override fun isEnpty(): Boolean = innerList.isEmpty()
  override fun contains (element: T) : Boolean = innerList.contains (e丄ement) override fun iterator () : Iterator<T> = innerList. iterator ()
  override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
}
```

이런 위임을 언어가 제공하는 일급 시민 기능으로 지원한다는 점이 코틀린의 장점이다.
인터페이스를 구현할 때, `by` 키워드를 통해 인터페이스에 대한 구현을 다른 객체에 위임 중이라는 사실을 명시할 수 있다.

```kotlin
class DelegatingCollection<T>(
  innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList {}
```

구현의 일부를 수정하고 싶을 경우 위에서 본 `override`를 사용해 해당 함수를 재구현하면 된다.

```kotlin
class CountingSet<T>(
  innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet {
  var objectsAdded = 0
  override fun add (element: T): Boolean { 
    objectsAdded++
    return innerSet.add(element)
  }
  override fun addAll(c: Collection<T>): Boolean {
    objectsAdded += c . size
    return innerSet.addAll(c)
  }
}
```

## 4. object 키워드 : 클래스 선언과 인스턴스 생성

`object` 키워드는  아래 두 상황에서 사용될 수 있다.

- 객체 선언(object declaration)
  - 싱글톤 정의
- 동반 객체(companion object)
  - 인스턴스 메소드는 아니지만, 어떤 클래스와 관련 있는 메소드와 팩토리 메소드를 담을 때 사용
  - 동반 객체가 포함된 클래스의 이름을 사용할 수 있다.
- 객체 식
  - 자바의 무명 내부 클래스(anonymous inner class) 대신 사용

### 객체 선언 : 싱글톤을 쉽게 만들기

코틀린은 객체 선언 기능을 통해 싱글톤을 언어에서 기본 지원한다.

```kotlin
object Payroll {
    val allEmployees = arrayListOf<Person>()
    fun calculateSalary() {
        for (person in allEmployees) {
            println(person.name)
        }
    }
}
```

객체 선언은 `object` 키워드로 시작한다.
이는 클래스를 정의하고, 그 클래스의 인스턴스를 만들어서 변수에 저장하는 모든 작업을 단 한 문장으로 처리한다.

`Comparator`는 두 객체를 인자로 받아 그 중 어느 객체가 더 큰지 알려주는 정수를 반환한다.
해당 구현은 보통 클래스마다 단 하나씩만 있으면 되므로, 객체 선언이 가장 좋은 방법이다.

```kotlin
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(o1: File, o2: File): Int {
        return o1.path.compareTo(o2.path, ignoreCase = true)
    }
}
```

일반 객체를 사용할 수 있는 곳에서는 항상 싱글턴 객체를 사용할 수 있다.

### 동반 객체 : 팩토리 메소드와 정적 멤버가 들어갈 장소

코틀린 언어는 자바 `static` 키워드를 지원하지 않기 때문에 정적 멤버가 없다.
대신, 코틀린에서는 정적 메소드 역할을 대신해 패키지 수준의 최상위 함수와 객체 선언을 사용한다.

클래스 내부 객체 중 `companion`을 붙이면 클래스의 동반 객체로 만들 수 있다.

```kotlin
class Foo {
    private fun foo() {}

    object Inner {
        fun fooPrint() {
            println("Fooh")
        }
    }
}

fun main() {
    Foo.Inner.fooPrint()
}
```

원래는 상위 클래스에서 내부 object를 불러와 함수를 호출하는 형태였다.
하지만 `companion`을 사용할 경우에는 자바의 정적 함수 및 필드처럼 바로 호출할 수 있게 된다.

```kotlin
class Foo {
    private fun foo() {}

    companion object {
        fun fooPrint() {
            println("Fooh")
        }
    }
}

fun main() {
    Foo.fooPrint()
}
```

이러한 동반 객체는 팩토리 메소드를 만드는데 유용하게 사용된다.

```kotlin
class User {
    val nickname: String

    constructor(email: String) {
        nickname = email.substringBefore('@')
    }

    constructor(facebookAccountId: Int) {
        nickname = getFacebookName(facebookAccountId)
    }
    
}
```

```kotlin
class User private constructor(val nickname: String) { 
    companion object { 
        fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
        fun newFacebookUser(accountId: Int) = User(getFacebookName(accountId)) 
    }
}
```

첫 코드는 생성자를 통해 유저를 만들어냈지만, 두 번째 코드는 생성자를 private로 만들어 접근하지 못하게 하였고,
동반 객체를 통해 알아서 유저 객체를 반환하는 팩토리 패턴으로 만들었다.

이런 패턴의 장점은 이미 존재하는 인스턴스에 해당하는 이메일 주소를 전달 받으면 새 인스턴스를 만들지 않고,
캐시에 있는 기존 인스턴스를 반환할 수 있게 추가 구현을 할 수 있다.