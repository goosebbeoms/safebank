import {useState, useEffect} from 'react';
import {Link, useLocation} from 'react-router-dom';
import {CreditCard, User, LogOut, Menu, X, BarChart3, Users, Wallet, ArrowRightLeft, ChevronDown} from 'lucide-react';
import {clsx} from 'clsx';

// 네비게이션 메뉴 아이템 타입 정의
interface NavItem {
    path: string;
    label: string;
    icon: React.ComponentType<{ className?: string }>;
    description?: string;
}

// 네비게이션 메뉴 데이터
const navigationItems: NavItem[] = [
    {
        path: '/dashboard',
        label: '대시보드',
        icon: BarChart3,
        description: '전체 현황 보기'
    },
    {
        path: '/members',
        label: '회원 관리',
        icon: Users,
        description: '회원 등록 및 조회'
    },
    {
        path: '/accounts',
        label: '계좌 관리',
        icon: Wallet,
        description: '계좌 개설 및 관리'
    },
    {
        path: '/transactions',
        label: '거래 관리',
        icon: ArrowRightLeft,
        description: '이체 및 거래내역',
    }
];

const NavLink = ({item, isActive}: { item: NavItem; isActive: boolean }) => {
    const Icon = item.icon;

    return (
        <Link
            to={item.path}
            className={clsx(
                'group relative flex items-center space-x-2 px-4 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ease-in-out',
                // 기본 상태
                'hover:bg-blue-50 hover:text-blue-700 hover:shadow-sm',
                // 활성 상태
                isActive
                    ? 'bg-blue-100 text-blue-700 shadow-sm border border-blue-200'
                    : 'text-gray-700',
                // 호버 효과
                'hover:scale-[1.02] hover:shadow-md'
            )}
        >
            {/* 아이콘 */}
            <Icon className={clsx(
                'h-4 w-4 transition-colors duration-200',
                isActive ? 'text-blue-600' : 'text-gray-500 group-hover:text-blue-600'
            )}/>

            {/* 라벨 */}
            <span className="relative">
                {item.label}
            </span>

            {/* 활성 인디케이터 */}
            {isActive && (
                <div
                    className="absolute bottom-0 left-1/2 transform -translate-x-1/2 translate-y-1 w-1 h-1 bg-blue-600 rounded-full"/>
            )}

            {/* 호버 시 툴팁 (선택사항) */}
            {item.description && (
                <div
                    className="absolute top-full left-1/2 transform -translate-x-1/2 mt-2 px-2 py-1 bg-gray-900 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-50">
                    {item.description}
                    <div
                        className="absolute bottom-full left-1/2 transform -translate-x-1/2 border-2 border-transparent border-b-gray-900"/>
                </div>
            )}
        </Link>
    );
};

// 드롭다운 메뉴 컴포넌트
const UserDropdown = () => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="relative">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-all duration-200"
            >
                <div
                    className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                    <User className="h-4 w-4 text-white"/>
                </div>
                <span className="text-sm font-medium hidden lg:block">관리자</span>
                <ChevronDown className={clsx(
                    'h-4 w-4 transition-transform duration-200',
                    isOpen ? 'rotate-180' : ''
                )}/>
            </button>

            {/* 드롭다운 메뉴 */}
            {isOpen && (
                <>
                    {/* 배경 오버레이 */}
                    <div
                        className="fixed inset-0 z-10"
                        onClick={() => setIsOpen(false)}
                    />

                    {/* 드롭다운 내용 */}
                    <div
                        className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-20">
                        <div className="px-4 py-2 border-b border-gray-100">
                            <p className="text-sm font-medium text-gray-900">관리자</p>
                            <p className="text-xs text-gray-500">admin@safebank.com</p>
                        </div>

                        <button
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center space-x-2">
                            <User className="h-4 w-4"/>
                            <span>프로필</span>
                        </button>

                        <button
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center space-x-2">
                            <LogOut className="h-4 w-4"/>
                            <span>로그아웃</span>
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export const Header = () => {
    const location = useLocation();
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);

    // 스크롤 감지
    useEffect(() => {
        const handleScroll = () => {
            setScrolled(window.scrollY > 10);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
        <header className={clsx(
            'sticky top-0 z-40 bg-white transition-all duration-300',
            scrolled
                ? 'shadow-lg border-b border-gray-200/50 backdrop-blur-sm bg-white/95'
                : 'shadow-sm border-b border-gray-200'
        )}>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center h-16">

                    {/* 로고 섹션 */}
                    <Link to="/dashboard" className="flex items-center space-x-3 group">
                        <div className="relative">
                            <div
                                className="w-10 h-10 bg-gradient-to-br from-blue-600 to-blue-700 rounded-xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-all duration-300 group-hover:scale-105">
                                <CreditCard className="h-5 w-5 text-white"/>
                            </div>
                            {/* 로고 글로우 효과 */}
                            <div
                                className="absolute inset-0 bg-gradient-to-br from-blue-600 to-blue-700 rounded-xl opacity-0 group-hover:opacity-20 transition-opacity duration-300 blur"/>
                        </div>
                        <div>
                            <h1 className="text-xl font-bold text-gray-900 group-hover:text-blue-700 transition-colors duration-200">
                                SafeBank
                            </h1>
                            <p className="text-xs text-gray-500 hidden sm:block">Digital Banking</p>
                        </div>
                    </Link>

                    {/* 데스크톱 내비게이션 */}
                    <nav className="hidden md:flex items-center space-x-2">
                        {navigationItems.map((item) => (
                            <NavLink
                                key={item.path}
                                item={item}
                                isActive={location.pathname === item.path}
                            />
                        ))}
                    </nav>

                    {/* 우측 사용자 메뉴 */}
                    <div className="flex items-center space-x-4">
                        {/* 사용자 드롭다운 */}
                        <UserDropdown/>

                        {/* 모바일 메뉴 버튼 */}
                        <button
                            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                            className="md:hidden p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all duration-200"
                        >
                            {mobileMenuOpen ? <X className="h-5 w-5"/> : <Menu className="h-5 w-5"/>}
                        </button>
                    </div>
                </div>

                {/* 모바일 메뉴 */}
                {mobileMenuOpen && (
                    <div className="md:hidden border-t border-gray-200 py-4 space-y-2">
                        {navigationItems.map((item) => {
                            const Icon = item.icon;
                            const isActive = location.pathname === item.path;

                            return (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    onClick={() => setMobileMenuOpen(false)}
                                    className={clsx(
                                        'flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-medium transition-all duration-200',
                                        isActive
                                            ? 'bg-blue-100 text-blue-700 border-l-4 border-blue-600'
                                            : 'text-gray-700 hover:bg-gray-50'
                                    )}
                                >
                                    <Icon className={clsx(
                                        'h-5 w-5',
                                        isActive ? 'text-blue-600' : 'text-gray-500'
                                    )}/>
                                    <div>
                                        <div>{item.label}</div>
                                        {item.description && (
                                            <div className="text-xs text-gray-500">{item.description}</div>
                                        )}
                                    </div>
                                </Link>
                            );
                        })}
                    </div>
                )}
            </div>
        </header>
    );
};