package com.holidaykeeper.holidaykeeper.repository;

import com.holidaykeeper.holidaykeeper.domain.Holiday;
import com.holidaykeeper.holidaykeeper.dto.request.HolidayGetRequest;
import com.holidaykeeper.holidaykeeper.dto.response.PageResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.holidaykeeper.holidaykeeper.domain.QHoliday.holiday;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryCustomImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    @Override
    public List<Holiday> getHolidays(Integer year, String CountryCode) {

        List<Long> holidayIds = queryFactory.select(holiday.id)
                .from(holiday)
                .where(
                        eqCountryCode(CountryCode),
                        eqYear(year)
                )
                .fetch();

        if (holidayIds.isEmpty()) {
            return List.of();
        }

        List<Holiday> holidays = queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.country).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.types).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.counties).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        return holidays;
    }

    @Override
    public PageResponse<Holiday> getHolidays(HolidayGetRequest request) {

        int page = request.getPage() != null ? request.getPage() : DEFAULT_PAGE_NUMBER;
        int pageSize = request.getSize() != null ? request.getSize() : DEFAULT_PAGE_SIZE;
        int offset = page == 0 ? 0 : page * pageSize;

        // 1. 전체 카운트 조회
        Long totalElements = queryFactory.select(holiday.count())
                .from(holiday)
                .where(
                        eqCountryCode(request.getCountryCode()),
                        eqYear(request.getYear()),
                        dateGoe(request.getFromDate()),
                        dateLoe(request.getToDate())
                )
                .fetchOne();

        long total = totalElements != null ? totalElements : 0L;

        // 2. 페이징된 Holiday ID 조회
        List<Long> holidayIds = queryFactory.select(holiday.id)
                .from(holiday)
                .where(
                        eqCountryCode(request.getCountryCode()),
                        eqYear(request.getYear()),
                        dateGoe(request.getFromDate()),
                        dateLoe(request.getToDate())
                )
                .offset(offset)
                .limit(pageSize)
                .fetch();

        // 3. ID가 없으면 빈 결과 반환
        if (holidayIds.isEmpty()) {
            return PageResponse.<Holiday>builder()
                    .content(List.of())
                    .page(page)
                    .size(pageSize)
                    .totalElements(total)
                    .totalPages(0)
                    .build();
        }

        // 4. Holiday + Country fetch join
        List<Holiday> content = queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.country).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        // 5. types fetch join (별도 쿼리)
        queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.types).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        // 6. counties fetch join (별도 쿼리)
        queryFactory.selectFrom(holiday)
                .distinct()
                .leftJoin(holiday.counties).fetchJoin()
                .where(holiday.id.in(holidayIds))
                .fetch();

        // 7. totalPages 계산
        int totalPages = (int) Math.ceil((double) total / pageSize);

        return PageResponse.<Holiday>builder()
                .content(content)
                .page(page)
                .size(pageSize)
                .totalElements(total)
                .totalPages(totalPages)
                .build();

    }

    private BooleanExpression eqYear(Integer year) {
        return year != null ? holiday.date.year().eq(year) : null;
    }

    private BooleanExpression eqCountryCode(String countryCode) {
        return countryCode != null ? holiday.country.code.eq(countryCode) : null;
    }

    private BooleanExpression dateGoe(LocalDate date) {
        return date != null ? holiday.date.goe(date) : null;
    }

    private BooleanExpression dateLoe(LocalDate date) {
        return date != null ? holiday.date.loe(date) : null;
    }
}
