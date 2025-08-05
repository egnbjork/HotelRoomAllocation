Requirements: Hotel Room Allocation System

R1: Room Inventory Configuration

The hotel room allocation system shall support a configurable number of premium and economy rooms for each occupancy request.

R2: Guest Offer Handling

The system shall accept a list of potential guests, each defined by a single numeric value representing their willingness to pay for one night.

R3: Premium Guest Assignment

Guests with a willingness to pay of EUR 100 or more shall be considered premium guests and must only be assigned to premium rooms.

R4: Economy Guest Assignment

Guests with a willingness to pay of less than EUR 100 shall be considered economy guests and shall be assigned to economy rooms if available.

R5: Guest Upgrade Policy

If premium rooms remain available after all premium guests have been assigned, the system shall upgrade economy guests to premium rooms in descending order of willingness to pay, only after all economy rooms are filled.

R6: Prioritization Logic

The system shall prioritize guests in descending order of willingness to pay, ensuring the highest-paying guests are always allocated rooms first, regardless of room category.

R7: Revenue Calculation

The system shall calculate and return the total revenue from both premium and economy room allocations based on the assigned guests’ willingness to pay.

R8: Room Usage Metrics

The system shall return the number of occupied rooms per category (premium, economy) in the output of each occupancy calculation.

R9: Input Validation

If the room counts or guest offers are invalid (e.g., negative values, nulls), the system shall reject the request with a descriptive error response.

R10: REST API Design

The /occupancy endpoint shall accept a POST request with the following input structure:

{
"premiumRooms": <int>,
"economyRooms": <int>,
"potentialGuests": [<double>, ...]
}

And return the following output structure:

{
"usagePremium": <int>,
"revenuePremium": <double>,
"usageEconomy": <int>,
"revenueEconomy": <double>
}
